package com.pange.genfee.portal.dao;

import com.pange.genfee.model.OmsOrderItem;
import com.pange.genfee.portal.domain.OmsOrderDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PortalOrderDao {

    /**
     * 释放sku锁定库存
     */
    int releaseSkuLockStock(@Param("itemList")List<OmsOrderItem> orderItemList);

    /**
     * 修改锁定库存和真实库存
     */
    int updateSkuStock(@Param("itemList") List<OmsOrderItem> orderItemList);

    /**
     * 获取超时订单列表
     */
    List<OmsOrderDetail> getTimeoutOrders(@Param("minutes") Integer minutes);

    /**
     * 批量修改订单状态
     * @param orderIds 订单id集合
     * @param status 状态
     */
    void updateOrderStatus(@Param("orderIds") List<Long> orderIds, @Param("status") Integer status);

    /**
     * 获取订单详情
     */
    OmsOrderDetail getDetail(@Param("orderId") Long orderId);
}
