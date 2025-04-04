package com.pange.genfee.portal.service.impl;

import com.pange.genfee.mapper.OmsOrderReturnApplyMapper;
import com.pange.genfee.model.OmsOrderReturnApply;
import com.pange.genfee.portal.domain.OmsOrderReturnApplyParam;
import com.pange.genfee.portal.service.OmsPortalOrderReturnApplyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @auther Pange
 * @description
 * @date {2025/4/4}
 */
@Service
public class OmsPortalOrderReturnApplyServiceImpl implements OmsPortalOrderReturnApplyService {
    @Autowired
    private OmsOrderReturnApplyMapper orderReturnApplyMapper;

    @Override
    public int create(OmsOrderReturnApplyParam returnApplyParam) {
        OmsOrderReturnApply orderReturnApply = new OmsOrderReturnApply();
        BeanUtils.copyProperties(returnApplyParam,orderReturnApply);
        orderReturnApply.setStatus(0);
        orderReturnApply.setCreateTime(new Date());
        return orderReturnApplyMapper.insert(orderReturnApply);
    }
}
