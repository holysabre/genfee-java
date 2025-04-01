package com.pange.genfee.portal.service;

import com.pange.genfee.model.SmsCoupon;
import com.pange.genfee.model.SmsCouponHistory;
import com.pange.genfee.portal.domain.CartPromotionItem;
import com.pange.genfee.portal.domain.SmsCouponHistoryDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UmsMemberCouponService {

    //添加优惠券
    @Transactional
    void add(Long couponId);

    //获取优惠券历史列表
    List<SmsCouponHistory> listHistory(Integer useStatus);

    //根据购物车信息获取可用优惠券列表
    List<SmsCouponHistoryDetail> listCart(List<CartPromotionItem> cartItemList, Integer type);

    //获取当前商品相关优惠券
    List<SmsCoupon> listByProduct(Long productId);

    //获取用户优惠券列表
    List<SmsCoupon> list(Integer useStatus);
}
