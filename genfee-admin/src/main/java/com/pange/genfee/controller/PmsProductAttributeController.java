package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.PmsProductAttrInfo;
import com.pange.genfee.dto.PmsProductAttributeParam;
import com.pange.genfee.model.PmsProductAttribute;
import com.pange.genfee.service.PmsProductAttributeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@RequestMapping("/productAttribute")
@Api(tags = {"产品属性管理"})
public class PmsProductAttributeController {

    @Autowired
    private PmsProductAttributeService productAttributeService;

    @GetMapping("/list/{cid}")
    @ApiOperation("产品属性列表")
    @ApiImplicitParams({@ApiImplicitParam(name = "type",value = "0:属性, 1:参数", required = true,paramType = "query",dataType = "integer")})
    public CommonResult<List<PmsProductAttribute>> list(
            @PathVariable Long cid,
            @RequestParam Integer type,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize
    ){
        List<PmsProductAttribute> list = productAttributeService.getList(cid,type,pageNum,pageSize);
        return CommonResult.success(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("产品属性详情")
    public CommonResult show(@PathVariable Long id){
        PmsProductAttribute productCategory = productAttributeService.getItem(id);
        return CommonResult.success(productCategory);
    }

    @PostMapping("/")
    @ApiOperation("新增产品属性")
    public CommonResult create(@RequestBody PmsProductAttributeParam productAttributeParam){
        int count = productAttributeService.create(productAttributeParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改产品属性")
    public CommonResult update(@PathVariable Long id, @RequestBody PmsProductAttributeParam productAttributeParam){
        int count = productAttributeService.update(id,productAttributeParam);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除产品属性")
    public CommonResult update(@PathVariable Long id){
        int count = productAttributeService.delete(id);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @GetMapping("/attrInfo/{productCategoryId}")
    @ApiOperation("根据商品分类id获取商品属性及属性分类")
    public CommonResult<List<PmsProductAttrInfo>> getAttrInfo(@PathVariable Long productCategoryId){
        List<PmsProductAttrInfo> list = productAttributeService.getProductAttrInfo(productCategoryId);
        return CommonResult.success(list);
    }
}
