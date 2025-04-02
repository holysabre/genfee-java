package com.pange.genfee.portal.service;

import com.pange.genfee.portal.domain.ConfirmOrderResult;
import com.pange.genfee.portal.domain.OrderParam;

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
    Map<String, Object> generateOrder(OrderParam orderParam);
}
