package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.UmsMenuNode;
import com.pange.genfee.service.UmsMenuService;
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
 * @date {2025/3/22}
 */
@RestController
@RequestMapping("/menu")
@Api(tags = {"菜单管理"})
public class UmsMenuController {

    @Autowired
    private UmsMenuService menuService;

    @GetMapping("/treeList")
    @ApiOperation("菜单树")
    public CommonResult<List<UmsMenuNode>> treeList(){
        List<UmsMenuNode> menuNodeList = menuService.treeList();

        return CommonResult.success(menuNodeList);
    }
}
