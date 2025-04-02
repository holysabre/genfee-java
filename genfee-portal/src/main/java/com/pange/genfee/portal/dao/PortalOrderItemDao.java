package com.pange.genfee.portal.dao;

import com.pange.genfee.model.OmsOrderItem;

import java.util.List;

public interface PortalOrderItemDao {

    /**
     * 插入order_item表
     */
    void insertList(List<OmsOrderItem> list);
}
