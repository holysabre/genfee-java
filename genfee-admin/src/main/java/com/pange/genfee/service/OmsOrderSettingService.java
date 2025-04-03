package com.pange.genfee.service;

import com.pange.genfee.model.OmsOrderSetting;

public interface OmsOrderSettingService {

    /**
     * 获取指定订单设置详情
     */
    OmsOrderSetting getItem(Long id);

    /**
     * 修改指定订单设置
     */
    int update(Long id, OmsOrderSetting orderSetting);
}
