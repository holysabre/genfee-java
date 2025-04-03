package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.model.OmsOrderSetting;
import com.pange.genfee.service.OmsOrderSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @auther Pange
 * @description
 * @date {2025/4/3}
 */
@RestController
@RequestMapping("/orderSetting")
@Api(tags = {"订单设置"})
public class OmsOrderSettingController {
    @Autowired
    private OmsOrderSettingService orderSettingService;

    @GetMapping("/{id}")
    @ApiOperation("获取订单设置详情")
    public CommonResult getItem(@PathVariable Long id){
        OmsOrderSetting orderSetting = orderSettingService.getItem(id);
        return CommonResult.success(orderSetting);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改订单设置")
    public CommonResult update(@PathVariable Long id, @RequestBody OmsOrderSetting orderSetting){
        int count = orderSettingService.update(id,orderSetting);
        if(count > 0){
            return CommonResult.success(count);
        }else{
            return CommonResult.failed();
        }
    }
}
