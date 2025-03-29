package com.pange.genfee.portal.controller;

import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.model.PmsBrand;
import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.portal.service.PmsPortalBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/28}
 */
@RestController
@RequestMapping("/brand")
@Api(tags = {"品牌管理"})
public class PmsPortalBrandController {

    @Autowired
    private PmsPortalBrandService portalBrandService;

    @GetMapping("recommendList")
    @ApiOperation("推荐品牌列表")
    public CommonResult<List<PmsBrand>> recommendList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "6") Integer pageSize
    ){
        List<PmsBrand> brandList = portalBrandService.recommandList(pageNum,pageSize);
        return CommonResult.success(brandList);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("品牌详情")
    public CommonResult detail(@PathVariable Long id){
        PmsBrand brand = portalBrandService.detail(id);
        return CommonResult.success(brand);
    }

    @GetMapping("/productList")
    @ApiOperation("品牌相关产品列表")
    public CommonResult<CommonPage<PmsProduct>> productList(
            @RequestParam Long brandId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "6") Integer pageSize
    ){
        CommonPage<PmsProduct> pagination = portalBrandService.productList(brandId, pageNum, pageSize);
        return CommonResult.success(pagination);
    }
}
