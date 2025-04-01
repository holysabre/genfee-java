package com.pange.genfee.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.pange.genfee.common.exception.Asserts;
import com.pange.genfee.mapper.*;
import com.pange.genfee.model.*;
import com.pange.genfee.portal.dao.SmsCouponHistoryDao;
import com.pange.genfee.portal.domain.CartPromotionItem;
import com.pange.genfee.portal.domain.SmsCouponHistoryDetail;
import com.pange.genfee.portal.service.UmsMemberCouponService;
import com.pange.genfee.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/3/31}
 */
@Service
public class UmsMemberCouponServiceImpl implements UmsMemberCouponService {
    @Autowired
    private SmsCouponMapper couponMapper;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private SmsCouponProductRelationMapper couponProductRelationMapper;
    @Autowired
    private SmsCouponProductCategoryRelationMapper couponProductCategoryRelationMapper;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private SmsCouponHistoryDao couponHistoryDao;

    @Override
    public void add(Long couponId) {
        UmsMember currentMember = memberService.getCurrentMember();
        SmsCoupon coupon = couponMapper.selectByPrimaryKey(couponId);
        if(coupon==null){
            Asserts.fail("优惠券不存在");
        }
        if(coupon.getCount() <= 0){
            Asserts.fail("优惠券已经领完了");
        }
        Date now = new Date();
        if(now.before(coupon.getEnableTime())){
            Asserts.fail("优惠券还没到领取时间");
        }
        //判断用户领取的优惠券是否超过限制
        SmsCouponHistoryExample couponHistoryExample = new SmsCouponHistoryExample();
        couponHistoryExample.createCriteria().andCouponIdEqualTo(couponId).andMemberIdEqualTo(currentMember.getId());
        long count = couponHistoryMapper.countByExample(couponHistoryExample);
        if(count >= coupon.getPerLimit()){
            Asserts.fail("您已经领取过该优惠券");
        }
        //生成领取优惠券历史
        SmsCouponHistory couponHistory = new SmsCouponHistory();
        couponHistory.setCouponId(couponId);
        couponHistory.setCouponCode(generateCouponCode(currentMember.getId()));
        couponHistory.setMemberId(currentMember.getId());
        couponHistory.setMemberNickname(currentMember.getNickname());
        couponHistory.setCreateTime(now);
        //主动领取
        couponHistory.setGetType(1);
        //未使用
        couponHistory.setUseStatus(0);
        couponHistoryMapper.insert(couponHistory);
        //修改优惠券的数量、领取数量
        coupon.setCount(coupon.getCount() - 1);
        coupon.setReceiveCount(coupon.getReceiveCount() + 1);
        couponMapper.updateByPrimaryKeySelective(coupon);
    }

    /**
     * 生成16位优惠码：时间戳后8位+4位随机数+用户id后4位
     */
    private String generateCouponCode(Long memberId){
        StringBuilder sb = new StringBuilder();
        Long currentTimeMillis = System.currentTimeMillis();
        String timeMillisStr = currentTimeMillis.toString();
        sb.append(timeMillisStr.substring(timeMillisStr.length() - 8));
        for(int i = 0; i < 4; i ++){
            sb.append(new Random().nextInt(10));
        }
        String memberIdStr = memberId.toString();
        if(memberIdStr.length() <= 4){
            sb.append(String.format("%04d",memberId));
        }else{
            sb.append(memberIdStr.substring(memberIdStr.length() - 4));
        }
        return sb.toString();
    }

    @Override
    public List<SmsCouponHistory> listHistory(Integer useStatus) {
        UmsMember currentMember = memberService.getCurrentMember();
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andMemberIdEqualTo(currentMember.getId()).andUseStatusEqualTo(useStatus);
        return couponHistoryMapper.selectByExample(example);
    }

    @Override
    public List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type) {
        UmsMember currentMember = memberService.getCurrentMember();
        Date now = new Date();
        //获取用户所有的优惠券列表
        List<SmsCouponHistoryDetail> allList = couponHistoryDao.getDetailList(currentMember.getId());
        List<SmsCouponHistoryDetail> enableList = new ArrayList<>();
        List<SmsCouponHistoryDetail> disableList = new ArrayList<>();
        for(SmsCouponHistoryDetail detail : allList){
            Integer useType = detail.getCoupon().getUseType();
            BigDecimal minPoint = detail.getCoupon().getMinPoint();
            Date endTime = detail.getCoupon().getEndTime();
            if(useType == 0){
                //全场通用
                //判断是否满足优惠起点
                //计算购物车商品的总价
                BigDecimal totalAmount = calcTotalAmount(cartItemList);
                if(now.before(endTime) && totalAmount.subtract(minPoint).intValue() > 0){
                    enableList.add(detail);
                }else{
                    disableList.add(detail);
                }
            } else if (useType == 1) {
                //指定分类
                //计算指定分类商品的总价
                List<Long> productCategoryIds = new ArrayList<>();
                for(SmsCouponProductCategoryRelation productCategoryRelation : detail.getProductCategoryRelationList()){
                    productCategoryIds.add(productCategoryRelation.getProductCategoryId());
                }
                BigDecimal totalAmount = calcTotalAmountByProductCategoryId(cartItemList,productCategoryIds);
                if(now.before(endTime) && totalAmount.intValue() > 0 && totalAmount.subtract(minPoint).intValue() > 0){
                    enableList.add(detail);
                }else{
                    disableList.add(detail);
                }
            } else if (useType == 2) {
                //指定商品
                //计算指定商品的总价
                List<Long> productIds = new ArrayList<>();
                for (SmsCouponProductRelation couponProductRelation : detail.getProductRelationList()){
                    productIds.add(couponProductRelation.getProductId());
                }
                BigDecimal totalAmount = calcTotalAmountByProductId(cartItemList,productIds);
                if(now.before(endTime) && totalAmount.intValue() > 0 && totalAmount.subtract(minPoint).intValue() > 0){
                    enableList.add(detail);
                }else{
                    disableList.add(detail);
                }
            }
        }
        if(type.equals(1)){
            return enableList;
        }else{
            return disableList;
        }
    }

    @Override
    public List<SmsCoupon> listByProduct(Long productId) {
        List<Long> allCouponIds = new ArrayList<>();
        //获取指定商品的优惠券列表
        SmsCouponProductRelationExample couponProductRelationExample = new SmsCouponProductRelationExample();
        couponProductRelationExample.createCriteria().andProductIdEqualTo(productId);
        List<SmsCouponProductRelation> cprList = couponProductRelationMapper.selectByExample(couponProductRelationExample);
        if(CollectionUtil.isNotEmpty(cprList)){
            List<Long> cprCouponIds = cprList.stream().map(SmsCouponProductRelation::getCouponId).collect(Collectors.toList());
            allCouponIds.addAll(cprCouponIds);
        }
        //获取指定分类的优惠券列表
        PmsProduct product = productMapper.selectByPrimaryKey(productId);
        SmsCouponProductCategoryRelationExample couponProductCategoryRelationExample = new SmsCouponProductCategoryRelationExample();
        couponProductCategoryRelationExample.createCriteria().andProductCategoryIdEqualTo(product.getProductCategoryId());
        List<SmsCouponProductCategoryRelation> cpcrList = couponProductCategoryRelationMapper.selectByExample(couponProductCategoryRelationExample);
        if(CollectionUtil.isNotEmpty(cpcrList)){
            List<Long> cpcrCouponIds = cpcrList.stream().map(SmsCouponProductCategoryRelation::getCouponId).collect(Collectors.toList());
            allCouponIds.addAll(cpcrCouponIds);
        }
        //所有优惠券
        Date now = new Date();
        SmsCouponExample couponExample = new SmsCouponExample();
        couponExample.createCriteria().andEndTimeGreaterThan(now)
                .andStartTimeLessThan(now)
                .andUseTypeEqualTo(0);
        if(CollectionUtil.isNotEmpty(allCouponIds)){
            couponExample.or(couponExample.createCriteria()
                    .andEndTimeGreaterThan(now)
                    .andStartTimeLessThan(now)
                    .andUseTypeEqualTo(0)
                    .andIdIn(allCouponIds)
            );
        }
        return couponMapper.selectByExample(couponExample);
    }

    @Override
    public List<SmsCoupon> list(Integer useStatus) {
        UmsMember member = memberService.getCurrentMember();
        return couponHistoryDao.getCouponList(member.getId(),useStatus);
    }

    private BigDecimal calcTotalAmount(List<CartPromotionItem> list){
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : list){
            BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
            total = total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return total;
    }

    private BigDecimal calcTotalAmountByProductCategoryId(List<CartPromotionItem> list, List<Long> productCategoryIds){
        BigDecimal total = new BigDecimal("0");
        for (CartPromotionItem item : list){
            if(!productCategoryIds.contains(item.getProductCategoryId())){
                continue;
            }
            BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
            total = total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return total;
    }

    private BigDecimal calcTotalAmountByProductId(List<CartPromotionItem> list, List<Long> productIds){
        BigDecimal total = new BigDecimal("0");
        for(CartPromotionItem item : list){
            if(!productIds.contains(item.getProductId())){
                continue;
            }
            BigDecimal realPrice = item.getPrice().subtract(item.getReduceAmount());
            total = total.add(realPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return total;
    }
}
