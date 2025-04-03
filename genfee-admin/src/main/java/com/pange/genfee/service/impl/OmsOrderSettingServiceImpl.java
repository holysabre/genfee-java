package com.pange.genfee.service.impl;

import com.pange.genfee.mapper.OmsOrderSettingMapper;
import com.pange.genfee.model.OmsOrderSetting;
import com.pange.genfee.service.OmsOrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @auther Pange
 * @description
 * @date {2025/4/3}
 */
@Service
public class OmsOrderSettingServiceImpl implements OmsOrderSettingService {
    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;

    @Override
    public OmsOrderSetting getItem(Long id) {
        return orderSettingMapper.selectByPrimaryKey(id);
    }

    @Override
    public int update(Long id, OmsOrderSetting orderSetting) {
        orderSetting.setId(id);
        return orderSettingMapper.updateByPrimaryKeySelective(orderSetting);
    }
}
