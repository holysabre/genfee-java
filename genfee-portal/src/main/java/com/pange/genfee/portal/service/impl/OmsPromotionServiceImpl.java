package com.pange.genfee.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.pange.genfee.model.OmsCartItem;
import com.pange.genfee.model.PmsProductFullReduction;
import com.pange.genfee.model.PmsProductLadder;
import com.pange.genfee.model.PmsSkuStock;
import com.pange.genfee.portal.dao.PortalProductDao;
import com.pange.genfee.portal.domain.CartPromotionItem;
import com.pange.genfee.portal.domain.PromotionProduct;
import com.pange.genfee.portal.service.OmsPromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/3/31}
 */
@Service
public class OmsPromotionServiceImpl implements OmsPromotionService {
    @Autowired
    private PortalProductDao portalProductDao;

    @Override
    public List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList) {
        //1.根据productId分组，以spu为到位进行计算优惠
        Map<Long, List<OmsCartItem>> productCartMap = groupCartItemBySpu(cartItemList);
        //2.查询所有商品的优惠相关信息
        List<PromotionProduct> promotionProductList = getPromotionProductList(cartItemList);
        //3.根据商品促销类型计算商品促销优惠金额
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        for(Map.Entry<Long,List<OmsCartItem>> entry : productCartMap.entrySet()){
            Long productId = entry.getKey();
            PromotionProduct promotionProduct = getPromotionProductById(productId,promotionProductList);
            List<OmsCartItem> cartItems = entry.getValue();
            Integer promotionType = promotionProduct.getPromotionType();
            //促销类型：0->没有促销使用原价;1->使用促销价；2->使用会员价；3->使用阶梯价格；4->使用满减价格；5->限时购
            switch (promotionType){
                case 1:
                    //使用促销价
                    for (OmsCartItem cartItem : cartItems){
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        BeanUtils.copyProperties(cartItem,cartPromotionItem);
                        cartPromotionItem.setPromotionMessage("单品促销");
                        PmsSkuStock originalSku = getOriginalSku(promotionProduct,cartItem.getProductSkuId());
                        BigDecimal originalPrice = originalSku.getPrice();
                        cartPromotionItem.setPrice(originalPrice);
                        cartPromotionItem.setReduceAmount(originalSku.getPrice().subtract(promotionProduct.getPromotionPrice()));
                        cartPromotionItem.setRealStock(originalSku.getStock() - originalSku.getLockStock());
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                    break;
                case 3:
                    //打折优惠
                    int count = getCartItemCount(cartItems);
                    PmsProductLadder ladder = getProductLadder(count,promotionProduct.getProductLadderList());
                    if(ladder != null){
                        for (OmsCartItem cartItem : cartItems){
                            CartPromotionItem cartPromotionItem = new CartPromotionItem();
                            BeanUtils.copyProperties(cartItem,cartPromotionItem);
                            cartPromotionItem.setPromotionMessage(getLadderPromotionMessage(ladder));
                            PmsSkuStock originalSku = getOriginalSku(promotionProduct,cartItem.getProductSkuId());
                            BigDecimal originalPrice = originalSku.getPrice();
                            cartPromotionItem.setPrice(originalPrice);
                            //商品原价-折扣*商品原价
                            cartPromotionItem.setReduceAmount(originalSku.getPrice().subtract(originalPrice.multiply(ladder.getDiscount())));
                            cartPromotionItem.setRealStock(originalSku.getStock() - originalSku.getLockStock());
                            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                            cartPromotionItemList.add(cartPromotionItem);
                        }
                    }else{
                        handleNoReduce(cartPromotionItemList,cartItems,promotionProduct);
                    }
                    break;
                case 4:
                    //满减优惠
                    BigDecimal totalAmount = getCartItemAmount(cartItems);
                    PmsProductFullReduction fullReduction = getProductFullReduction(totalAmount,promotionProduct.getProductFullReductionList());
                    if(fullReduction != null){
                        for (OmsCartItem cartItem : cartItems){
                            CartPromotionItem cartPromotionItem = new CartPromotionItem();
                            BeanUtils.copyProperties(cartItem,cartPromotionItem);
                            cartPromotionItem.setPromotionMessage(getFullReductionPromotionMessage(fullReduction));
                            PmsSkuStock originalSku = getOriginalSku(promotionProduct,cartItem.getProductSkuId());
                            BigDecimal originalPrice = originalSku.getPrice();
                            cartPromotionItem.setPrice(originalPrice);
                            //(商品原价/总价)*优惠金额
                            cartPromotionItem.setReduceAmount(originalPrice.divide(totalAmount, RoundingMode.HALF_EVEN).multiply(fullReduction.getReducePrice()));
                            cartPromotionItem.setRealStock(originalSku.getStock() - originalSku.getLockStock());
                            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                            cartPromotionItemList.add(cartPromotionItem);
                        }
                    }else{
                        handleNoReduce(cartPromotionItemList,cartItems,promotionProduct);
                    }
                    break;
                default:
                    handleNoReduce(cartPromotionItemList,cartItems,promotionProduct);
                    break;
            }
        }

        return cartPromotionItemList;
    }

    private Map<Long, List<OmsCartItem>> groupCartItemBySpu(List<OmsCartItem> cartItemList){
        Map<Long, List<OmsCartItem>> productCartMap = new TreeMap<>();
        for (OmsCartItem cartItem : cartItemList){
            List<OmsCartItem> productCartItemMap = productCartMap.get(cartItem.getProductId());
            if(productCartItemMap == null){
                productCartItemMap = new ArrayList<>();
                productCartItemMap.add(cartItem);
                productCartMap.put(cartItem.getProductId(),productCartItemMap);
            }else{
                productCartItemMap.add(cartItem);
            }
        }
        return productCartMap;
    }

    /**
     * 查询商品促销信息
     */
    private List<PromotionProduct> getPromotionProductList(List<OmsCartItem> cartItemList){
        List<Long> productIds = cartItemList.stream().map(OmsCartItem::getProductId).collect(Collectors.toList());
        return portalProductDao.getPromotionProductList(productIds);
    }

    /**
     * 更具商品id获取优惠信息
     */
    private PromotionProduct getPromotionProductById(Long productId, List<PromotionProduct> promotionProductList){
        List<PromotionProduct> filteredList = promotionProductList.stream().filter(item -> item.getId().equals(productId)).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(filteredList)){
            return filteredList.get(0);
        }
        return null;
    }

    /**
     * 无优惠
     */
    private void handleNoReduce(List<CartPromotionItem> cartPromotionItemList,List<OmsCartItem>cartItems,PromotionProduct promotionProduct){
        for (OmsCartItem cartItem : cartItems){
            CartPromotionItem cartPromotionItem = new CartPromotionItem();
            BeanUtils.copyProperties(cartItem,cartPromotionItem);
            cartPromotionItem.setPromotionMessage("无优惠");
            cartPromotionItem.setReduceAmount(new BigDecimal("0"));
            PmsSkuStock originalSku = getOriginalSku(promotionProduct,cartItem.getProductSkuId());
            if(originalSku != null){
                cartPromotionItem.setRealStock(originalSku.getStock() - originalSku.getLockStock());
            }
            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
            cartPromotionItemList.add(cartPromotionItem);
        }
    }

    /**
     * 获取商品的sku
     */
    private PmsSkuStock getOriginalSku(PromotionProduct promotionProduct,Long skuId){
        for (PmsSkuStock skuStock : promotionProduct.getSkuStockList()){
            if(skuStock.getId().equals(skuId)){
                return skuStock;
            }
        }
        return null;
    }

    /**
     * 获取购物车中指定商品的数量
     */
    private int getCartItemCount(List<OmsCartItem> cartItemList){
        int count = 0;
        for (OmsCartItem cartItem : cartItemList){
            count = count + cartItem.getQuantity();
        }
        return count;
    }

    /**
     * 获取匹配的打折优惠
     */
    private PmsProductLadder getProductLadder(int count, List<PmsProductLadder> ladderList){
        //按数量从小到大排序
        ladderList.sort(new Comparator<PmsProductLadder>() {
            @Override
            public int compare(PmsProductLadder o1, PmsProductLadder o2) {
                return o2.getCount() - o1.getCount();
            }
        });
        for (PmsProductLadder ladder : ladderList){
            if(count >= ladder.getCount()){
                return ladder;
            }
        }
        return null;
    }

    /**
     * 获取打折优惠的促销品信息
     */
    private String getLadderPromotionMessage(PmsProductLadder ladder){
        StringBuilder sb = new StringBuilder();
        sb.append("打折优惠：");
        sb.append("满");
        sb.append(ladder.getCount().toString());
        sb.append("件，");
        sb.append("打");
        sb.append(ladder.getDiscount().multiply(new BigDecimal("10")));
        sb.append("折");
        return sb.toString();
    }

    /**
     * 获取匹配的满减优惠
     */
    private PmsProductFullReduction getProductFullReduction(BigDecimal totalAmount,List<PmsProductFullReduction> fullReductionList){
        //按金额从高到低
        fullReductionList.sort(new Comparator<PmsProductFullReduction>() {
            @Override
            public int compare(PmsProductFullReduction o1, PmsProductFullReduction o2) {
                return o1.getFullPrice().subtract(o2.getFullPrice()).intValue();
            }
        });
        for (PmsProductFullReduction fullReduction : fullReductionList){
            if(fullReduction.getFullPrice().subtract(totalAmount).intValue() <= 0){
                return fullReduction;
            }
        }
        return null;
    }

    /**
     * 获取满减优惠的促销品信息
     */
    private String getFullReductionPromotionMessage(PmsProductFullReduction fullReduction){
        StringBuilder sb = new StringBuilder();
        sb.append("满减优惠：");
        sb.append("满");
        sb.append(fullReduction.getFullPrice().toString());
        sb.append("元，");
        sb.append("减");
        sb.append(fullReduction.getReducePrice().toString());
        sb.append("元");
        return sb.toString();
    }

    /**
     * 获取购物车的总金额
     */
    private BigDecimal getCartItemAmount(List<OmsCartItem> cartItemList){
        BigDecimal totalAmount = new BigDecimal("0");
        for(OmsCartItem cartItem : cartItemList){
            totalAmount = totalAmount.add(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }
        return totalAmount;
    }
}
