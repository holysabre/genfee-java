package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.OmsOrderQueryParam;
import com.pange.genfee.model.OmsOrder;
import com.pange.genfee.service.OmsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/4/3}
 */
@RestController
@RequestMapping("/order")
@Api(tags = {"订单管理"})
public class OmsOrderController {
    @Autowired
    private OmsOrderService orderService;


    @GetMapping("/list")
    @ApiOperation("订单列表")
    public CommonResult<CommonPage<OmsOrder>> list(
            @RequestBody OmsOrderQueryParam queryParam,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize
            ){
        List<OmsOrder> orderList = orderService.list(queryParam,pageNum,pageSize);
        return CommonResult.success(CommonPage.restPage(orderList));
    }
}
