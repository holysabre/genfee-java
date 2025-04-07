package com.pange.genfee.search.controller;

import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.search.domain.EsProduct;
import com.pange.genfee.search.domain.EsProductRelatedInfo;
import com.pange.genfee.search.service.EsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/4/7}
 */
@RestController
@RequestMapping("/esProduct")
@Api(tags = {"搜索商品管理"})
public class EsProductController {
    @Autowired
    private EsProductService esProductService;

    @PostMapping("/importAll")
    @ApiOperation("导入所有商品到ES")
    public CommonResult importAllList(){
        int count = esProductService.importAll();
        return CommonResult.success(count);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除指定ID商品")
    public CommonResult delete(@PathVariable Long id){
        esProductService.delete(id);
        return CommonResult.success(null);
    }

    @DeleteMapping("/delete/batch")
    @ApiOperation("删除指定ID集合商品")
    public CommonResult delete(@RequestParam List<Long> ids){
        esProductService.delete(ids);
        return CommonResult.success(null);
    }

    @PostMapping("/create/{id}")
    @ApiOperation("创建指定ID商品")
    public CommonResult create(@PathVariable Long id){
        EsProduct product = esProductService.create(id);
        if(product != null){
            return CommonResult.success(product);
        }
        return CommonResult.failed();
    }

    @GetMapping("/search/simple")
    @ApiOperation("简单搜索")
    public CommonResult<CommonPage<EsProduct>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "6") Integer pageSize
    ){
        Page<EsProduct> pagination = esProductService.search(keyword, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(pagination));
    }

    @GetMapping("search")
    @ApiOperation(value = "综合搜索、筛选、排序")
    @ApiImplicitParam(name = "sort", value = "排序字段:0->按相关度；1->按新品；2->按销量；3->价格从低到高；4->价格从高到低",
            defaultValue = "0", allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
    public CommonResult<CommonPage<EsProduct>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long productCategoryId,
            @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @RequestParam(required = false, defaultValue = "6") Integer pageSize,
            @RequestParam(required = false, defaultValue = "0") Integer sort
    ){
        Page<EsProduct> pagination = esProductService.search(keyword, brandId, productCategoryId, pageNum, pageSize, sort);
        return CommonResult.success(CommonPage.restPage(pagination));
    }

    @GetMapping("/recommend/{id}")
    @ApiOperation(value = "根据商品id推荐商品")
    public CommonResult<CommonPage<EsProduct>> recommend(@PathVariable Long id,
                                                         @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                         @RequestParam(required = false, defaultValue = "5") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductService.recommend(id, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(esProductPage));
    }

    @GetMapping("/search/relate")
    @ApiOperation(value = "获取搜索的相关品牌、分类及筛选属性")
    public CommonResult<EsProductRelatedInfo> searchRelatedInfo(@RequestParam(required = false) String keyword) {
        EsProductRelatedInfo productRelatedInfo = esProductService.searchRelatedInfo(keyword);
        return CommonResult.success(productRelatedInfo);
    }
}
