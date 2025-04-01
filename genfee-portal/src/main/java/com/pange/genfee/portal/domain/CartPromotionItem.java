package com.pange.genfee.portal.domain;

import com.pange.genfee.model.OmsCartItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @auther Pange
 * @description
 * @date {2025/3/31}
 */
@Getter
@Setter
public class CartPromotionItem extends OmsCartItem {
    @ApiModelProperty("促销活动信息")
    private String promotionMessage;
    @ApiModelProperty("促销活动减去的金额")
    private BigDecimal reduceAmount;
    @ApiModelProperty("真实库存=剩余库存-锁定库存")
    private Integer realStock;
    @ApiModelProperty("赠送的积分")
    private Integer integration;
    @ApiModelProperty("赠送的成长值")
    private Integer growth;
}
