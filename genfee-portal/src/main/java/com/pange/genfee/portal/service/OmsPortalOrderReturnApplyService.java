package com.pange.genfee.portal.service;

import com.pange.genfee.portal.domain.OmsOrderReturnApplyParam;

/**
 * 前台订单退货管理
 */
public interface OmsPortalOrderReturnApplyService {

    /**
     * 提交退货申请
     */
    int create(OmsOrderReturnApplyParam returnApplyParam);
}
