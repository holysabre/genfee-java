package com.pange.genfee.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.common.exception.Asserts;
import com.pange.genfee.common.service.RedisService;
import com.pange.genfee.mapper.*;
import com.pange.genfee.model.*;
import com.pange.genfee.portal.component.CancelOrderSender;
import com.pange.genfee.portal.dao.PortalOrderDao;
import com.pange.genfee.portal.dao.PortalOrderItemDao;
import com.pange.genfee.portal.domain.*;
import com.pange.genfee.portal.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/4/1}
 */
@Service
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private UmsMemberReceiveAddressMapper memberReceiveAddressMapper;
    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PortalOrderItemDao orderItemDao;
    @Autowired
    private PortalOrderDao orderDao;
    @Autowired
    private CancelOrderSender cancelOrderSender;

    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;
    @Value("${redis.database}")
    private String REDIS_DATABASE;

    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> ids) {
        ConfirmOrderResult result = new ConfirmOrderResult();
        UmsMember currentMember = memberService.getCurrentMember();
        //购物车信息
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),ids);
        result.setCartPromotionItemList(cartPromotionItemList);
        //用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = memberReceiveAddressService.list();
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        //用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList,1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        //用户积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSettingList = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSettingList);
        //用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        //计算
        result.setCalcAmount(calcCartAmount(cartPromotionItemList));
        return result;
    }

    @Override
    public Map<String, Object> generateOrder(OrderParam orderParam) {
        if(orderParam.getMemberReceiveAddressId() == null){
            Asserts.fail("请选择收货地址");
        }
        UmsMember currentMember = memberService.getCurrentMember();
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),orderParam.getCartIds());
        //判断库存
        if(!hasStock(cartPromotionItemList)){
            Asserts.fail("库存不足");
        }
        for(CartPromotionItem cartPromotionItem : cartPromotionItemList){
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductBrand(cartPromotionItem.getProductBrand());
            orderItem.setProductSn(cartPromotionItem.getProductSn());
            orderItem.setProductPrice(cartPromotionItem.getPrice());
            orderItem.setProductQuantity(cartPromotionItem.getQuantity());
            orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
            orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
            orderItemList.add(orderItem);
        }
        //判断使用优惠券
        if(orderParam.getCouponId() == null){
            //不使用优惠券
            for (OmsOrderItem orderItem : orderItemList){
                orderItem.setCouponAmount(new BigDecimal("0"));
            }
        }else{
            //使用优惠券
            SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
            if(couponHistoryDetail == null){
                Asserts.fail("优惠券不可用");
            }
            handleCouponAmount(orderItemList,couponHistoryDetail);
        }
        //判断使用积分
        if(orderParam.getUseIntegration() == null || orderParam.getUseIntegration().equals(0)){
            //不使用积分
            for (OmsOrderItem orderItem : orderItemList){
                orderItem.setIntegrationAmount(new BigDecimal("0"));
            }
        }else{
            //使用积分
            BigDecimal totalAmount = calcOrderTotalAmount(orderItemList);
            BigDecimal integrationAmount = getUseIntegrationAmount(orderParam.getUseIntegration(),totalAmount,currentMember,orderParam.getCouponId() != null);
            if(integrationAmount.compareTo(new BigDecimal("0")) == 0){
                Asserts.fail("积分不可用");
            }else{
                for (OmsOrderItem orderItem : orderItemList){
                    BigDecimal perAmount = orderItem.getProductPrice().divide(totalAmount,3,RoundingMode.HALF_EVEN).multiply(integrationAmount);
                    orderItem.setIntegrationAmount(perAmount);
                }
            }
        }
        //计算真实金额
        handleRealAmount(orderItemList);
        //锁定库存
        lockStock(cartPromotionItemList);
        //合计
        OmsOrder order = new OmsOrder();
        order.setTotalAmount(calcOrderTotalAmount(orderItemList));
        order.setDiscountAmount(new BigDecimal("0"));
        order.setFreightAmount(new BigDecimal("0"));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if(orderParam.getCouponId() == null){
            order.setCouponAmount(new BigDecimal("0"));
        }else{
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if(orderParam.getUseIntegration() == null){
            order.setUseIntegration(0);
            order.setIntegrationAmount(new BigDecimal("0"));
        }else{
            order.setUseIntegration(orderParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }
        order.setPayAmount(calcPayAmount(order));
        //入库
        order.setMemberId(currentMember.getId());
        order.setMemberUsername(currentMember.getUsername());
        order.setCreateTime(new Date());
        //支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        //订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        //订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        //订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        //收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = memberReceiveAddressMapper.selectByPrimaryKey(orderParam.getMemberReceiveAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        //0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        //计算赠送积分
        order.setIntegration(calcGiftIntegration(orderItemList));
        //计算赠送成长值
        order.setGrowth(calcGiftGrowth(orderItemList));
        //生成订单号
        order.setOrderSn(generateOrderSn(order));
        //设置自动收货天数
        List<OmsOrderSetting> orderSettingList = orderSettingMapper.selectByExample(new OmsOrderSettingExample());
        if(CollectionUtil.isNotEmpty(orderSettingList)){
            order.setAutoConfirmDay(orderSettingList.get(0).getConfirmOvertime());
        }
        //插入order表和order_item表
        orderMapper.insert(order);
        for (OmsOrderItem orderItem : orderItemList){
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemDao.insertList(orderItemList);
        //如使用优惠券更新优惠券使用状态
        if(orderParam.getCouponId() != null){
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        //如使用积分需要扣除积分
        if(orderParam.getUseIntegration() != null){
            memberService.updateIntegration(currentMember.getId(), orderParam.getUseIntegration());
        }
        //删除购物车中的下单商品
        deleteCartItemList(cartPromotionItemList,currentMember.getId());
        //发送延迟消息取消订单


        Map<String, Object> result = new HashMap<>();
        result.put("order",order);
        result.put("orderItemList",orderItemList);

        return result;
    }

    @Override
    public void cancelOrder(Long orderId) {
        //查询未付款的订单
        OmsOrderExample orderExample = new OmsOrderExample();
        orderExample.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0)
                .andDeleteStatusEqualTo(0);
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        if(CollectionUtil.isEmpty(orderList)){
            return;
        }
        OmsOrder cancelOrder = orderList.get(0);
        if(cancelOrder != null){
            //修改订单状态为取消
            cancelOrder.setStatus(4);
            orderMapper.updateByPrimaryKeySelective(cancelOrder);
            //解除订单商品库存锁定
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
            if(CollectionUtil.isNotEmpty(orderItemList)){
                orderDao.releaseSkuLockStock(orderItemList);
            }
            //修改优惠券使用状态
            updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
            //返还使用的积分
            if(cancelOrder.getUseIntegration() != null){
                UmsMember member = memberService.getById(cancelOrder.getMemberId());
                memberService.updateIntegration(member.getId(),member.getIntegration() + cancelOrder.getUseIntegration());
            }
        }
    }

    @Override
    public Integer cancelTimeOutOrder() {
        Integer count = 0;
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        //查询超时、未支付的订单
        List<OmsOrderDetail> timeoutOrders = orderDao.getTimeoutOrders(orderSetting.getNormalOrderOvertime());
        if(CollectionUtil.isEmpty(timeoutOrders)){
            return count;
        }
        //修改订单状态为交易取消
        List<Long> orderIds = new ArrayList<>();
        for (OmsOrderDetail orderDetail : timeoutOrders){
            orderIds.add(orderDetail.getId());
        }
        orderDao.updateOrderStatus(orderIds,4);
        for (OmsOrderDetail orderDetail : timeoutOrders){
            //释放库存
            orderDao.releaseSkuLockStock(orderDetail.getOrderItemList());
            //修改优惠券使用状态
            if(orderDetail.getCouponId() != null){
                updateCouponStatus(orderDetail.getCouponId(), orderDetail.getMemberId(),0);
            }
            //返还积分
            if(orderDetail.getUseIntegration() != null){
                UmsMember member = memberService.getCurrentMember();
                memberService.updateIntegration(member.getId(), member.getIntegration() + orderDetail.getUseIntegration());
            }
        }
        return timeoutOrders.size();
    }

    @Override
    public Integer paySuccess(Long orderId, Integer payType) {
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setStatus(1);
        order.setPaymentTime(new Date());
        order.setPayType(payType);
        orderMapper.updateByPrimaryKeySelective(order);
        //释放锁定库存，扣减真实库存
        OmsOrderDetail orderDetail = orderDao.getDetail(orderId);
        return orderDao.updateSkuStock(orderDetail.getOrderItemList());
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        cancelOrderSender.sendMessage(orderId,delayTimes);
    }

    @Override
    public CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrderExample orderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = orderExample.createCriteria()
                .andDeleteStatusEqualTo(0)
                .andMemberIdEqualTo(member.getId());
        status = status == -1 ? null : status;
        if(status != null){
            criteria.andStatusEqualTo(status);
        }
        orderExample.setOrderByClause("create_time desc");
        PageHelper.startPage(pageNum,pageSize);
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        CommonPage<OmsOrder> orderPage = CommonPage.restPage(orderList);

        CommonPage<OmsOrderDetail> resultPage = new CommonPage<>();
        resultPage.setPageNum(orderPage.getPageNum());
        resultPage.setPageSize(orderPage.getPageSize());
        resultPage.setTotalPage(orderPage.getTotalPage());
        resultPage.setTotal(orderPage.getTotal());
        if(CollectionUtil.isEmpty(orderList)){
            return resultPage;
        }

        //设置订单项目列表
        List<Long> orderIds = new ArrayList<>();
        for (OmsOrder order : orderList){
            orderIds.add(order.getId());
        }
        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria().andOrderIdIn(orderIds);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        List<OmsOrderDetail> orderDetailList = new ArrayList<>();
        for(OmsOrder order : orderList){
            OmsOrderDetail orderDetail = new OmsOrderDetail();
            BeanUtils.copyProperties(order, orderDetail);
            List<OmsOrderItem> filteredOrderItemsList = orderItemList.stream()
                    .filter(item -> item.getOrderId().equals(order.getId()))
                    .collect(Collectors.toList());
            orderDetail.setOrderItemList(filteredOrderItemsList);
            orderDetailList.add(orderDetail);
        }
        resultPage.setList(orderDetailList);
        return resultPage;
    }

    @Override
    public OmsOrderDetail detail(Long orderId) {
        OmsOrderDetail orderDetail = new OmsOrderDetail();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        BeanUtils.copyProperties(order,orderDetail);
        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orderDetail.getId());
        orderDetail.setOrderItemList(orderItemMapper.selectByExample(orderItemExample));
        return orderDetail;
    }

    @Override
    public void confirmReceiveOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if(!order.getMemberId().equals(member.getId())){
            Asserts.fail("无操作权限");
        }
        if(order.getStatus() != 2){
            Asserts.fail("订单还没发货");
        }
        order.setStatus(3);
        order.setConfirmStatus(1);
        order.setReceiveTime(new Date());
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if(!order.getMemberId().equals(member.getId())){
            Asserts.fail("无操作权限");
        }
        if(order.getStatus() == 3 || order.getStatus() == 4){
            order.setDeleteStatus(1);
            orderMapper.updateByPrimaryKeySelective(order);
        }else{
            Asserts.fail("只能删除已完成或已关闭的订单");
        }
    }

    /**
     * 计算购物车中商品的价格
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList){
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal("0"));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList){
            totalAmount = totalAmount.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            promotionAmount = promotionAmount.add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount));
        return calcAmount;
    }

    /**
     * 判断下单中的商品是否有库存
     */
    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList){
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList){
            Integer realStock = cartPromotionItem.getRealStock();
            //真实库存存在且大于0，且大于下单的数量
            if(realStock == null || realStock <= 0 || cartPromotionItem.getQuantity() > cartPromotionItem.getRealStock()){
                 return false;
            }
        }
        return true;
    }

    /**
     * 获取使用的优惠券
     */
    private SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> cartPromotionItemList, Long couponId){
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList,1);
        for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList){
            if(couponHistoryDetail.getCouponId().equals(couponId)){
                return couponHistoryDetail;
            }
        }
        return null;
    }

    /**
     * 处理优惠券的优惠信息
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail){
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        if(coupon.getType() == 0){
            //全场通用
            calcPerCouponAmount(orderItemList,coupon);
        } else if (coupon.getType() == 1) {
            //指定分类
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail,orderItemList);
            calcPerCouponAmount(couponOrderItemList,coupon);
        }else if(coupon.getType() == 2){
            //指定商品
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail,orderItemList);
            calcPerCouponAmount(couponOrderItemList,coupon);
        }
    }

    /**
     * 对每个订单项目进行优惠券金额分摊的计算
     */
    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon){
        BigDecimal totalAmount = calcOrderTotalAmount(orderItemList);
        //(商品价格/可用商品总价)*优惠券面额
        for (OmsOrderItem orderItem : orderItemList){
            BigDecimal couponAmount = orderItem.getProductPrice().divide(totalAmount,3, RoundingMode.HALF_EVEN).multiply(coupon.getAmount());
            orderItem.setCouponAmount(couponAmount);
        }
    }

    /**
     * 计算订单总金额
     */
    private BigDecimal calcOrderTotalAmount(List<OmsOrderItem> orderItemList){
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem orderItem : orderItemList){
            totalAmount = totalAmount.add(orderItem.getProductPrice().multiply(new BigDecimal(orderItem.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 根据优惠券类型获取可用的订单项目
     */
    private List<OmsOrderItem> getCouponOrderItemByRelation(SmsCouponHistoryDetail couponHistoryDetail,List<OmsOrderItem> orderItemList){
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        List<OmsOrderItem> couponOrderItemList = new ArrayList<>();
        if(coupon.getType() == 1){
            //指定分类
            List<Long> categoryIds = new ArrayList<>();
            for(SmsCouponProductCategoryRelation categoryRelation : couponHistoryDetail.getProductCategoryRelationList()){
                categoryIds.add(categoryRelation.getProductCategoryId());
            }
            for (OmsOrderItem orderItem : orderItemList){
                if(categoryIds.contains(orderItem.getProductCategoryId())){
                    couponOrderItemList.add(orderItem);
                }else{
                    orderItem.setCouponAmount(new BigDecimal("0"));
                }
            }
        } else if (coupon.getType() == 2) {
            //指定商品
            List<Long> productIds = new ArrayList<>();
            for (SmsCouponProductRelation couponProductRelation : couponHistoryDetail.getProductRelationList()){
                productIds.add(couponProductRelation.getProductId());
            }
            for(OmsOrderItem orderItem : orderItemList){
                if(productIds.contains(orderItem.getProductId())){
                    couponOrderItemList.add(orderItem);
                }else{
                    orderItem.setCouponAmount(new BigDecimal("0"));
                }
            }
        }
        return couponOrderItemList;
    }

    /**
     * 获取可用积分抵扣金额
     * @param useIntegration 使用的积分
     * @param totalAmount 订单总金额
     * @param currentMember 当前用户
     * @param hasCoupon 是否使用优惠券
     */
    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember currentMember, boolean hasCoupon){
        BigDecimal zeroAmount = new BigDecimal("0");
        //判断用户积分是否足够
        if(useIntegration.compareTo(currentMember.getIntegration()) > 0){
            return zeroAmount;
        }
        //根据积分规则是否满足
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        //是否能和优惠券共用
        if(hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)){
            return zeroAmount;
        }
        //判断最低使用积分门槛
        if(useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0){
            return zeroAmount;
        }
        //是否超过订单抵用最高百分比
        BigDecimal integrationAmount = new BigDecimal(useIntegration).divide(new BigDecimal(integrationConsumeSetting.getUseUnit()),2,RoundingMode.HALF_EVEN);
        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder()).divide(new BigDecimal("100"),2,RoundingMode.HALF_EVEN);
        if(integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0){
            return zeroAmount;
        }
        return integrationAmount;
    }

    /**
     * 计算真实金额
     */
    private void handleRealAmount(List<OmsOrderItem> orderItemList){
        for (OmsOrderItem orderItem : orderItemList){
            orderItem.setRealAmount(orderItem.getProductPrice()
                .subtract(orderItem.getPromotionAmount()
                .subtract(orderItem.getCouponAmount())
                .subtract(orderItem.getIntegrationAmount())));
        }
    }

    /**
     * 锁定库存
     */
    private void lockStock(List<CartPromotionItem> cartPromotionItemList){
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList){
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(cartPromotionItem.getProductSkuId());
            skuStock.setLockStock(skuStock.getLockStock() + cartPromotionItem.getQuantity());
            skuStockMapper.updateByPrimaryKeySelective(skuStock);
        }
    }

    /**
     * 计算订单优惠金额
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList){
        BigDecimal promotionAmount = new BigDecimal("0");
        for (OmsOrderItem orderItem : orderItemList){
            promotionAmount = promotionAmount.add(orderItem.getPromotionAmount());
        }
        return promotionAmount;
    }

    /**
     * 获取订单促销信息
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList){
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList){
            sb.append(orderItem.getPromotionName());
            sb.append(";");
        }
        String info = sb.toString();
        if(info.endsWith(";")){
            info = info.substring(0,info.length() - 1);
        }
        return info;
    }

    /**
     * 计算订单优惠券总金额
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList){
        BigDecimal couponAmount = new BigDecimal("0");
        for (OmsOrderItem orderItem : orderItemList){
            couponAmount = couponAmount.add(orderItem.getCouponAmount());
        }
        return couponAmount;
    }

    /**
     * 计算订单抵扣积分总金额
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList){
        BigDecimal integrationAmount = new BigDecimal("0");
        for (OmsOrderItem orderItem : orderItemList){
            integrationAmount = integrationAmount.add(orderItem.getIntegrationAmount());
        }
        return integrationAmount;
    }

    /**
     * 计算应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order){
        return order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
    }

    /**
     * 计算订单赠送积分
     */
    private Integer calcGiftIntegration(List<OmsOrderItem> orderItemList){
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList){
            sum = orderItem.getGiftIntegration() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算订单赠送成长值
     */
    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList){
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList){
            sum = orderItem.getGiftGrowth() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 生成订单号
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OmsOrder order){
        StringBuilder sb = new StringBuilder();
        String sdf = new SimpleDateFormat("yyyyMMdd").format(new Date());
        sb.append(sdf);
        sb.append(String.format("%02d",order.getSourceType()));
        sb.append(String.format("%02d",order.getPayType()));
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ORDER_ID + sdf;
        Long incr = redisService.incr(key,1);
        String incrStr = incr.toString();
        if(incrStr.length() <= 6){
            sb.append(String.format("%06d",incr));
        }else{
            sb.append(incrStr);
        }
        return sb.toString();
    }

    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus){
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andCouponIdEqualTo(couponId)
                .andMemberIdEqualTo(memberId)
                .andUseStatusEqualTo(useStatus == 0 ? 1 : 0);
        List<SmsCouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(example);
        if(CollectionUtil.isNotEmpty(couponHistoryList)){
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseStatus(useStatus);
            couponHistory.setUseTime(new Date());
            couponHistoryMapper.updateByPrimaryKeySelective(couponHistory);
        }
    }

    /**
     * 删除购物车中的下单商品
     * @param cartPromotionItemList 选中的购物车列表
     * @param memberId 会员id
     */
    private void deleteCartItemList(List<CartPromotionItem> cartPromotionItemList, Long memberId){
        List<Long> cartIds = new ArrayList<>();
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList){
            cartIds.add(cartPromotionItem.getId());
        }
        cartItemService.delete(memberId,cartIds);
    }

}
