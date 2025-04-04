package com.pange.genfee.portal.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.portal.domain.OmsOrderReturnApplyParam;
import com.pange.genfee.portal.service.OmsPortalOrderReturnApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther Pange
 * @description
 * @date {2025/4/4}
 */
@RestController
@RequestMapping("order/returnApply")
@Api(tags = {"退货申请管理"})
public class OmsPortalOrderReturnApplyController {
    @Autowired
    private OmsPortalOrderReturnApplyService orderReturnApplyService;

    @PostMapping("/create")
    @ApiOperation("申请退货")
    public CommonResult create(@RequestBody OmsOrderReturnApplyParam returnApplyParam){
        int count  = orderReturnApplyService.create(returnApplyParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
