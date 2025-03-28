package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.PmsProductParam;
import com.pange.genfee.dto.PmsProductResult;
import com.pange.genfee.service.PmsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @auther Pange
 * @description
 * @date {2025/3/28}
 */
@RestController
@RequestMapping("/product")
@Api(tags = {"产品管理"})
public class PmsSkuStockController {

    @Autowired
    private PmsProductService productService;

    @PostMapping
    @ApiOperation("新增产品")
    public CommonResult create(@RequestBody PmsProductParam productParam){
        int count = productService.create(productParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取产品详情")
    public CommonResult show(@PathVariable Long id){
        PmsProductResult productResult = productService.getUpdateInfo(id);
        return CommonResult.success(productResult);
    }
}
