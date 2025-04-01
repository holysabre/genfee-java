package com.pange.genfee.portal.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.model.OmsCartItem;
import com.pange.genfee.model.SmsCoupon;
import com.pange.genfee.model.SmsCouponHistory;
import com.pange.genfee.portal.domain.CartPromotionItem;
import com.pange.genfee.portal.domain.SmsCouponHistoryDetail;
import com.pange.genfee.portal.service.OmsCartItemService;
import com.pange.genfee.portal.service.UmsMemberCouponService;
import com.pange.genfee.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/31}
 */
@RestController
@RequestMapping("/member/coupon")
@Api(tags = {"用户优惠券"})
public class UmsMemberCouponController {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private OmsCartItemService cartItemService;


    @PostMapping("/add/{couponId}")
    @ApiOperation("领取指定优惠券")
    public CommonResult add(@PathVariable Long couponId){
        memberCouponService.add(couponId);
        return CommonResult.success(null,"领取成功");
    }

    @GetMapping("/listHistory")
    @ApiOperation("获取优惠券领取历史列表")
    @ApiImplicitParam(name = "useStatus", value = "优惠券使用状态：0->未使用,1->已使用,2->已过期", allowableValues = "0,1,2",paramType = "query", dataType = "integer")
    public CommonResult<List<SmsCouponHistory>> listHistory(@RequestParam(value = "useStatus",required = false) Integer useStatus){
        List<SmsCouponHistory> list = memberCouponService.listHistory(useStatus);
        return CommonResult.success(list);
    }

    @GetMapping("/list")
    @ApiOperation("获取会员优惠券列表")
    @ApiImplicitParam(name = "useStatus", value = "优惠券使用状态：0->未使用,1->已使用,2->已过期", allowableValues = "0,1,2", paramType = "query", dataType = "integer")
    public CommonResult<List<SmsCoupon>> list(@RequestParam(value = "useStatus",required = false) Integer useStatus){
        List<SmsCoupon> list = memberCouponService.list(useStatus);
        return CommonResult.success(list);
    }

    @GetMapping("/listCart")
    @ApiOperation("获取会员购物车商品优惠券列表")
    public CommonResult<List<SmsCouponHistoryDetail>> listCart(@RequestParam Integer type){
        List<CartPromotionItem> cartPromotionItems = cartItemService.listPromotion(memberService.getCurrentMember().getId(),null);
        List<SmsCouponHistoryDetail> couponHistoryList = memberCouponService.listCart(cartPromotionItems,type);
        return CommonResult.success(couponHistoryList);
    }
}
