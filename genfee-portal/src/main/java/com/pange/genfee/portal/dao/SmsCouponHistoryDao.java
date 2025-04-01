package com.pange.genfee.portal.dao;

import com.pange.genfee.model.SmsCoupon;
import com.pange.genfee.portal.domain.SmsCouponHistoryDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmsCouponHistoryDao {

    /**
     * 获取指定用户已领优惠券列表
     */
    List<SmsCouponHistoryDetail> getDetailList(@Param("memberId") Long memberId);

    /**
     * 获取指定用户优惠券列表
     */
    List<SmsCoupon> getCouponList(@Param("memberId") Long memberId,@Param("useStatus") Integer useStatus);
}
