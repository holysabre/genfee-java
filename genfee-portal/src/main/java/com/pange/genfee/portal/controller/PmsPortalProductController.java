package com.pange.genfee.portal.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.portal.domain.PmsPortalProductDetail;
import com.pange.genfee.portal.domain.PmsProductCategoryNode;
import com.pange.genfee.portal.service.PmsPortalProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/29}
 */
@RestController
@RequestMapping("/product")
@Api(tags = {"前台商品"})
public class PmsPortalProductController {
    @Autowired
    private PmsPortalProductService portalProductService;

    @GetMapping("/search")
    @ApiImplicitParam(name = "sort", value = "排序字段:0->按相关度；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
        defaultValue = "0", allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
    @ApiOperation("搜索商品列表")
    public CommonResult<List<PmsProduct>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long productCategoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "6") Integer pageSize,
            @RequestParam(defaultValue = "0") Integer sort
    ){
        List<PmsProduct> productList = portalProductService.search(keyword,brandId,productCategoryId,pageNum,pageSize,sort);
        return CommonResult.success(productList);
    }

    @GetMapping("/categoryTreeList")
    @ApiOperation("商品分类树形列表")
    public CommonResult<List<PmsProductCategoryNode>> categoryTreeList(){
        List<PmsProductCategoryNode> nodes = portalProductService.categoryTreeList();
        return CommonResult.success(nodes);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("商品详情")
    public CommonResult detail(@PathVariable Long id){
        PmsPortalProductDetail productDetail = portalProductService.detail(id);
        return CommonResult.success(productDetail);
    }
}
