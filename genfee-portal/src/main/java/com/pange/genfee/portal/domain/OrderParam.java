package com.pange.genfee.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/4/2}
 */
@Getter
@Setter
public class OrderParam {
    @ApiModelProperty("用户收货地址id")
    private Long memberReceiveAddressId;
    @ApiModelProperty("优惠券id")
    private Long couponId;
    @ApiModelProperty("使用的积分")
    private Integer useIntegration;
    @ApiModelProperty("支付方式")
    private Integer payType;
    @ApiModelProperty("选中的购物车id集合")
    private List<Long> cartIds;
}
