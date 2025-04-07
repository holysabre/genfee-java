package com.pange.genfee.portal.controller;

import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.portal.domain.MemberProductCollection;
import com.pange.genfee.portal.service.MemberCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @auther Pange
 * @description
 * @date {2025/4/6}
 */
@RestController
@RequestMapping("/member/productCollection")
@Api(tags = {"会员商品收藏管理"})
public class MemberProductCollectionController {
    @Autowired
    private MemberCollectionService memberCollectionService;

    @PostMapping("/add")
    @ApiOperation("添加商品收藏")
    public CommonResult add(@RequestBody MemberProductCollection productCollection){
        int count = memberCollectionService.add(productCollection);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @GetMapping("/list")
    @ApiOperation("会员商品收藏列表")
    public CommonResult<CommonPage<MemberProductCollection>> list(
            @RequestParam(required = false,defaultValue = "1") Integer pageNum,
            @RequestParam(required = false,defaultValue = "6") Integer pageSize){
        Page<MemberProductCollection> page = memberCollectionService.list(pageNum,pageSize);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @DeleteMapping("/{productId")
    @ApiOperation("删除收藏的商品")
    public CommonResult delete(@PathVariable Long productId){
        int count = memberCollectionService.delete(productId);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @GetMapping("/{productId}")
    @ApiOperation("收藏的商品习详情")
    public CommonResult detail(@PathVariable Long productId){
        MemberProductCollection productCollection = memberCollectionService.detail(productId);
        return CommonResult.success(productCollection);
    }

    @DeleteMapping("/clear")
    @ApiOperation("清空收藏的商品")
    public CommonResult clear(){
        memberCollectionService.clear();
        return CommonResult.success(null);
    }
}
