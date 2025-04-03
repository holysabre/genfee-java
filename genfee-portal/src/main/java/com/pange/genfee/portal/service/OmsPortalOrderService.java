package com.pange.genfee.portal.service;

import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.portal.domain.ConfirmOrderResult;
import com.pange.genfee.portal.domain.OmsOrderDetail;
import com.pange.genfee.portal.domain.OrderParam;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface OmsPortalOrderService {

    /**
     * 根据用户购物车信息生成确认单信息
     */
    ConfirmOrderResult generateConfirmOrder(List<Long> ids);

    /**
     * 生成订单
     */
    @Transactional
    Map<String, Object> generateOrder(OrderParam orderParam);

    /**
     * 取消订单
     */
    @Transactional
    void cancelOrder(Long orderId);

    /**
     * 自动取消超时订单
     */
    @Transactional
    Integer cancelTimeOutOrder();

    /**
     * 支付成功后的回调
     */
    @Transactional
    Integer paySuccess(Long orderId, Integer payType);

    /**
     * 发送取消订单延迟消息
     * @param orderId 订单id
     */
    void sendDelayMessageCancelOrder(Long orderId);

    /**
     * 分页获取订单列表
     */
    CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 根据订单id获取订单详情
     */
    OmsOrderDetail detail(Long orderId);

    /**
     * 订单确认收货
     */
    void confirmReceiveOrder(Long orderId);

    /**
     * 删除订单
     */
    void deleteOrder(Long orderId);
}
