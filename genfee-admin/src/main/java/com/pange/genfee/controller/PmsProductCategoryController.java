package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.PmsProductCategoryParam;
import com.pange.genfee.model.PmsProductCategory;
import com.pange.genfee.service.PmsProductCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/27}
 */
@RestController
@RequestMapping("/productCategory")
@Api(tags = {"产品分类"})
public class PmsProductCategoryController {

    @Autowired
    private PmsProductCategoryService productCategoryService;

    @GetMapping("/")
    @ApiOperation("产品分类列表")
    public CommonResult<List<PmsProductCategory>> list(
            @RequestParam(defaultValue = "0") Long parentId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
        ){
        List<PmsProductCategory> list = productCategoryService.getList(parentId,pageNum,pageSize);
        return CommonResult.success(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("产品分类详情")
    public CommonResult show(@PathVariable Long id){
        PmsProductCategory productCategory = productCategoryService.getItem(id);
        return CommonResult.success(productCategory);
    }

    @PostMapping("/")
    @ApiOperation("新增产品分类")
    public CommonResult create(@RequestBody PmsProductCategoryParam productCategoryParam){
        int count = productCategoryService.create(productCategoryParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改产品分类")
    public CommonResult update(@PathVariable Long id, @RequestBody PmsProductCategoryParam productCategoryParam){
        int count = productCategoryService.update(id,productCategoryParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除产品分类")
    public CommonResult update(@PathVariable Long id){
        int count = productCategoryService.delete(id);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
