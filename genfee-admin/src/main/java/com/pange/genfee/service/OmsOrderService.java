package com.pange.genfee.service;

import com.pange.genfee.dto.*;
import com.pange.genfee.model.OmsOrder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OmsOrderService {
    /**
     * 订单列表
     */
    List<OmsOrder> list(OmsOrderQueryParam orderQueryParam, Integer pageNum, Integer pageSize);

    /**
     * 订单详情
     */
    OmsOrderDetail detail(Long id);

    /**
     * 批量发货
     */
    @Transactional
    int delivery(List<OmsOrderDeliveryParam> deliveryParams);

    /**
     * 批量关闭订单
     */
    @Transactional
    int close(List<Long> ids, String note);

    /**
     * 批量删除
     */
    int delete(List<Long> ids);

    /**
     * 修改订单收货人信息
     */
    @Transactional
    int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam);

    /**
     * 修改订单费用信息
     */
    @Transactional
    int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam);

    /**
     * 修改订单备注
     */
    @Transactional
    int updateOrderNote(Long id, String note, Integer status);
}
