package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.PmsBrandParam;
import com.pange.genfee.service.PmsBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther Pange
 * @description
 * @date {2025/3/22}
 */
@RestController
@RequestMapping("/brand")
@Api(tags = "品牌管理")
public class PmsBrandController {
    @Autowired
    private PmsBrandService brandService;

    @PostMapping("/create")
    @ApiOperation("新增品牌")
    public CommonResult create(@Validated @RequestBody PmsBrandParam brandParam){
        int count = brandService.create(brandParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
