package com.pange.genfee.portal.domain;

import com.pange.genfee.model.SmsCoupon;
import com.pange.genfee.model.SmsCouponHistory;
import com.pange.genfee.model.SmsCouponProductCategoryRelation;
import com.pange.genfee.model.SmsCouponProductRelation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/31}
 */
@Getter
@Setter
public class SmsCouponHistoryDetail extends SmsCouponHistory {
    @ApiModelProperty("优惠券信息")
    private SmsCoupon coupon;
    @ApiModelProperty("优惠券关联的商品列表")
    private List<SmsCouponProductRelation> productRelationList;
    @ApiModelProperty("优惠券关联商品分类列表")
    private List<SmsCouponProductCategoryRelation> productCategoryRelationList;
}
