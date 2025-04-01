package com.pange.genfee.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.pange.genfee.mapper.OmsCartItemMapper;
import com.pange.genfee.model.OmsCartItem;
import com.pange.genfee.model.OmsCartItemExample;
import com.pange.genfee.model.UmsMember;
import com.pange.genfee.portal.dao.PortalProductDao;
import com.pange.genfee.portal.domain.CartProduct;
import com.pange.genfee.portal.domain.CartPromotionItem;
import com.pange.genfee.portal.service.OmsCartItemService;
import com.pange.genfee.portal.service.OmsPromotionService;
import com.pange.genfee.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/3/29}
 */
@Service
public class OmsCartItemServiceImpl implements OmsCartItemService {

    @Autowired
    private OmsCartItemMapper cartItemMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private PortalProductDao portalProductDao;
    @Autowired
    private OmsPromotionService promotionService;

    @Override
    public List<OmsCartItem> list(Long memberId) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        return cartItemMapper.selectByExample(example);
    }

    @Override
    public int add(OmsCartItem cartItem) {
        int count = 0;
        UmsMember currentMember = memberService.getCurrentMember();
        cartItem.setMemberId(currentMember.getId());
        cartItem.setMemberNickname(currentMember.getNickname());
        cartItem.setDeleteStatus(0);
        OmsCartItem existCartItem = getCartItem(cartItem);
        if(existCartItem == null){
            cartItem.setCreateDate(new Date());
            count = cartItemMapper.insert(cartItem);
        }else{
            existCartItem.setModifyDate(new Date());
            existCartItem.setQuantity(existCartItem.getQuantity() + cartItem.getQuantity());
            count = cartItemMapper.updateByPrimaryKeySelective(existCartItem);
        }

        return count;
    }

    private OmsCartItem getCartItem(OmsCartItem cartItem){
        OmsCartItemExample example = new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria()
                .andMemberIdEqualTo(cartItem.getMemberId())
                .andProductIdEqualTo(cartItem.getProductId());
        if(cartItem.getProductSkuId() != null){
            criteria.andProductSkuIdEqualTo(cartItem.getProductSkuId());
        }
        List<OmsCartItem> cartItemList = cartItemMapper.selectByExample(example);
        if(CollectionUtil.isNotEmpty(cartItemList)){
            return cartItemList.get(0);
        }
        return null;
    }

    @Override
    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setQuantity(quantity);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andIdEqualTo(id).andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(cartItem,example);
    }

    @Override
    public int delete(Long memberId, List<Long> ids) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andIdIn(ids).andMemberIdEqualTo(memberId);
        return cartItemMapper.deleteByExample(example);
    }

    @Override
    public int clear(Long memberId) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        return cartItemMapper.deleteByExample(example);
    }

    @Override
    public CartProduct getCartProduct(Long productId) {
        return portalProductDao.getCartProduct(productId);
    }

    @Override
    public int updateAttr(OmsCartItem cartItem) {
        OmsCartItem updateCartItem = new OmsCartItem();
        updateCartItem.setId(cartItem.getId());
        updateCartItem.setDeleteStatus(1);
        updateCartItem.setModifyDate(new Date());
        cartItemMapper.updateByPrimaryKeySelective(updateCartItem);
        cartItem.setId(null);
        int count = add(cartItem);
        return count;
    }

    @Override
    public List<CartPromotionItem> listPromotion(Long memberId, List<Long> cartIds) {
        OmsCartItemExample cartItemExample = new OmsCartItemExample();
        cartItemExample.createCriteria().andMemberIdEqualTo(memberId);
        List<OmsCartItem> cartItemList = cartItemMapper.selectByExample(cartItemExample);
        if(CollectionUtil.isNotEmpty(cartIds)){
            cartItemList = cartItemList.stream().filter(item -> cartIds.contains(item.getId())).collect(Collectors.toList());
        }
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(cartItemList)){
            cartPromotionItemList = promotionService.calcCartPromotion(cartItemList);

        }
        return cartPromotionItemList;
    }
}
