package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.PmsProductAttributeCategoryItem;
import com.pange.genfee.model.PmsProductAttributeCategory;
import com.pange.genfee.service.PmsProductAttributeCategoryService;
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
@RequestMapping("/productAttributeCategory")
@Api(tags = {"产品属性分类"})
public class PmsProductAttributeCategoryController {

    @Autowired
    private PmsProductAttributeCategoryService productAttributeCategoryService;

    @GetMapping("/")
    @ApiOperation("产品属性分类列表")
    public CommonResult<List<PmsProductAttributeCategory>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
        ){
        List<PmsProductAttributeCategory> list = productAttributeCategoryService.getList(pageNum,pageSize);
        return CommonResult.success(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("产品属性分类详情")
    public CommonResult show(@PathVariable Long id){
        PmsProductAttributeCategory productCategory = productAttributeCategoryService.getItem(id);
        return CommonResult.success(productCategory);
    }

    @PostMapping("/")
    @ApiOperation("新增产品属性分类")
    public CommonResult create(@RequestParam String name){
        int count = productAttributeCategoryService.create(name);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改产品属性分类")
    public CommonResult update(@PathVariable Long id, @RequestParam String name){
        int count = productAttributeCategoryService.update(id,name);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除产品属性分类")
    public CommonResult update(@PathVariable Long id){
        int count = productAttributeCategoryService.delete(id);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @GetMapping("/list/withAttr")
    @ApiOperation("获取所有商品属性分类及其下属性")
    public CommonResult<List<PmsProductAttributeCategoryItem>> getListWithAttr()
    {
        List<PmsProductAttributeCategoryItem> list = productAttributeCategoryService.getListWithAttribute();
        return CommonResult.success(list);
    }
}
