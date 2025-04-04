package com.pange.genfee.dao;

import com.pange.genfee.dto.OmsOrderDeliveryParam;
import com.pange.genfee.dto.OmsOrderDetail;
import com.pange.genfee.dto.OmsOrderQueryParam;
import com.pange.genfee.model.OmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OmsOrderDao {

    /**
     * 查询订单
     * @param queryParam 查询条件
     */
    List<OmsOrder> getList(@Param("queryParam") OmsOrderQueryParam queryParam);

    /**
     * 订单详情
     */
    OmsOrderDetail getDetail(@Param("orderId") Long id);

    /**
     * 批量发货
     */
    int delivery(@Param("deliveryParamList") List<OmsOrderDeliveryParam> deliveryParamList);
}
