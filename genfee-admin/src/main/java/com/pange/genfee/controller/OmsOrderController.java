package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.OmsMoneyInfoParam;
import com.pange.genfee.dto.OmsOrderDeliveryParam;
import com.pange.genfee.dto.OmsOrderQueryParam;
import com.pange.genfee.dto.OmsReceiverInfoParam;
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

    @PostMapping("/update/delivery")
    @ApiOperation("批量发货")
    public CommonResult delivery(@RequestBody List<OmsOrderDeliveryParam> deliveryParamList) {
        int count = orderService.delivery(deliveryParamList);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/update/close")
    @ApiOperation("批量关闭订单")
    public CommonResult close(@RequestParam("ids") List<Long> ids, @RequestParam String note) {
        int count = orderService.close(ids, note);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/delete")
    @ApiOperation("批量删除订单")
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = orderService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/update/receiverInfo")
    @ApiOperation("修改收货人信息")
    public CommonResult updateReceiverInfo(@RequestBody OmsReceiverInfoParam receiverInfoParam) {
        int count = orderService.updateReceiverInfo(receiverInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/update/moneyInfo")
    @ApiOperation("修改订单费用信息")
    public CommonResult updateReceiverInfo(@RequestBody OmsMoneyInfoParam moneyInfoParam) {
        int count = orderService.updateMoneyInfo(moneyInfoParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/update/note")
    @ApiOperation("备注订单")
    public CommonResult updateNote(@RequestParam("id") Long id,
                                   @RequestParam("note") String note,
                                   @RequestParam("status") Integer status) {
        int count = orderService.updateOrderNote(id, note, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
