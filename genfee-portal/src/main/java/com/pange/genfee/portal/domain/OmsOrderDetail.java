package com.pange.genfee.portal.domain;

import com.pange.genfee.model.OmsOrder;
import com.pange.genfee.model.OmsOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 包含商品信息的订单详情
 * @auther Pange
 * @description
 * @date {2025/4/3}
 */
@Getter
@Setter
public class OmsOrderDetail extends OmsOrder {
    @ApiModelProperty("订单项目列表")
    private List<OmsOrderItem> orderItemList;
}
