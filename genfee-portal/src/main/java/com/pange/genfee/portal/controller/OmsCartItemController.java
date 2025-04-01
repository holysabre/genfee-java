package com.pange.genfee.portal.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.model.OmsCartItem;
import com.pange.genfee.model.UmsMember;
import com.pange.genfee.portal.domain.CartProduct;
import com.pange.genfee.portal.service.OmsCartItemService;
import com.pange.genfee.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/30}
 */
@RestController
@RequestMapping("cart")
@Api(tags = {"购物车"})
public class OmsCartItemController {

    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberService memberService;

    @GetMapping("/list")
    @ApiOperation("购物车列表")
    public CommonResult<List<OmsCartItem>> list(){
        UmsMember currentMember = memberService.getCurrentMember();
        List<OmsCartItem> cartItemList = cartItemService.list(currentMember.getId());
        return CommonResult.success(cartItemList);
    }


    @PostMapping("/add")
    @ApiOperation("添加购物车")
    public CommonResult add(@RequestBody OmsCartItem cartItem){
        int count = cartItemService.add(cartItem);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/update/quantity")
    @ApiOperation("修改购物车中指定商品的数量")
    public CommonResult updateQuantity(@RequestParam Long id, @RequestParam Integer quantity){
        UmsMember currentMember = memberService.getCurrentMember();
        int count = cartItemService.updateQuantity(id,currentMember.getId(),quantity);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @GetMapping("/getProduct/{productId}")
    @ApiOperation("获取购物车中指定商品的规格信息")
    public CommonResult getCartProduct(@PathVariable Long productId){
        CartProduct cartProduct = cartItemService.getCartProduct(productId);
        return CommonResult.success(cartProduct);
    }

    @PostMapping("update/attr")
    @ApiOperation("修改购物车中商品的规格")
    public CommonResult updateAttr(@RequestBody OmsCartItem cartItem){
        int count = cartItemService.updateAttr(cartItem);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @DeleteMapping("delete")
    @ApiOperation("删除购物车中的商品")
    public CommonResult delete(@RequestParam List<Long> ids){
        UmsMember currentMember = memberService.getCurrentMember();
        int count = cartItemService.delete(currentMember.getId(),ids);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @DeleteMapping("clear")
    @ApiOperation("清空购物车")
    public CommonResult clear(){
        UmsMember currentMember = memberService.getCurrentMember();
        int count = cartItemService.clear(currentMember.getId());
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
