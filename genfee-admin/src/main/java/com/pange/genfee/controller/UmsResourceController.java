package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.model.UmsResource;
import com.pange.genfee.service.UmsResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/18}
 */

@RestController
@RequestMapping("/resource")
@Api(tags = {"UmsResourceController","资源管理"})
public class UmsResourceController {

    @Autowired
    private UmsResourceService resourceService;

    @GetMapping("/listAll")
    @ApiOperation("资源列表")
    public CommonResult<List<UmsResource>> listAll(){
        List<UmsResource> resourceList = resourceService.listAll();

        return CommonResult.success(resourceList);
    }

}
