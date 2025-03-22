package com.pange.genfee.portal.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.portal.domain.MemberRegisterParam;
import com.pange.genfee.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther Pange
 * @description
 * @date {2025/3/19}
 */
@RestController
@RequestMapping("/sso")
@Api(tags = {"UmsMemberController","用户管理"})
public class UmsMemberController {

    @Autowired
    private UmsMemberService memberService;
    @Value("${jwt.tokenHeader}")
    private String TOKEN_HEADER;

    @PostMapping("/getAuthCode")
    @ApiOperation("获取验证码")
    @ResponseBody
    public CommonResult getAuthCode(@RequestParam String phone){
        memberService.generateAuthCode(phone);
        return CommonResult.success(null);
    }

    @PostMapping("/verifyAuthCode")
    @ApiOperation("校验验证码")
    @ResponseBody
    public CommonResult verifyAuthCode(@RequestParam String phone,@RequestParam String authCode){
        boolean isVerify = memberService.verifyAuthCode(phone,authCode);
        if(!isVerify){
            return CommonResult.failed("验证码错误");
        }
        return CommonResult.success(null);
    }

    @PostMapping("/login")
    @ApiOperation("登录")
    public CommonResult login(@RequestParam String username,@RequestParam String password){
        String token = memberService.login(username,password);
        if(token == null){
            return CommonResult.unauthorized(token);
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        tokenMap.put("tokenHead",TOKEN_HEADER);
        return CommonResult.success(tokenMap);
    }

    @PostMapping("/register")
    @ApiOperation("注册")
    public CommonResult register(@RequestBody MemberRegisterParam memberRegisterParam){
        memberService.register(memberRegisterParam);
        return CommonResult.success(null);
    }
}
