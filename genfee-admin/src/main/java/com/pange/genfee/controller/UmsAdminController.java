package com.pange.genfee.controller;

import com.pange.genfee.common.api.CommonResult;
import com.pange.genfee.dto.UmsAdminLoginParam;
import com.pange.genfee.dto.UmsAdminParam;
import com.pange.genfee.dto.UpdateAdminPasswordParam;
import com.pange.genfee.model.UmsAdmin;
import com.pange.genfee.service.UmsAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther Pange
 * @description
 * @date {2025/3/20}
 */
@RestController
@RequestMapping("/admin")
@Api(tags = {"UmsAdminController","管理员管理"})
public class UmsAdminController {

    @Autowired
    private UmsAdminService adminService;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @PostMapping("login")
    @ApiOperation("登录")
    public CommonResult login(@RequestBody UmsAdminLoginParam adminLoginParam){
       String token = adminService.login(adminLoginParam.getUsername(),adminLoginParam.getPassword());
       if(token == null){
           return CommonResult.validateFailed("用户名或密码错误");
       }
       Map<String, String> tokenMap = new HashMap<>();
       tokenMap.put("token",token);
       tokenMap.put("tokenHead",tokenHead);
       return CommonResult.success(tokenMap,"登录成功");
    }

    @GetMapping("/refreshToken")
    @ApiOperation("刷新token")
    public CommonResult refreshToken(HttpServletRequest request){
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        if(token == null){
            CommonResult.failed("token已过期");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        tokenMap.put("tokenHead",tokenHead);
        return CommonResult.success(tokenMap);
    }

    @PostMapping("/logout")
    @ApiOperation("登出")
    public CommonResult logout(){
        return CommonResult.success(null);
    }

    @PostMapping("/register")
    @ApiOperation("注册")
    public CommonResult register(@RequestBody UmsAdminParam adminParam){
        UmsAdmin umsAdmin = adminService.register(adminParam);
        if(umsAdmin == null){
            return CommonResult.failed("注册失败");
        }else{
            return CommonResult.success("注册成功");
        }
    }

    @GetMapping("/list")
    @ApiOperation("管理员列表")
    public CommonResult<List<UmsAdmin>> list(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        List<UmsAdmin> adminList = adminService.list(keyword,pageSize,pageNum);

        return CommonResult.success(adminList,"success");
    }

    @GetMapping("/info")
    @ApiOperation("获取当前登录的用户")
    public CommonResult info(Principal principal)
    {
        if(principal == null){
            return CommonResult.unauthorized(null);
        }
        String username = principal.getName();
        UmsAdmin admin = adminService.getAdminByUsername(username);
        Map<String,String> data = new HashMap<>();
        data.put("username",admin.getUsername());
        data.put("icon",admin.getIcon());

        return CommonResult.success(data,"success");
    }

    @GetMapping("/{id}")
    @ApiOperation("获取指定管理员")
    public CommonResult getItem(@PathVariable Long id){
        UmsAdmin admin = adminService.getItem(id);
        return CommonResult.success(admin);
    }

    @PostMapping("/update/{id}")
    @ApiOperation("修改指定管理员")
    public CommonResult update(@PathVariable Long id, @RequestBody UmsAdmin admin){
        int count = adminService.update(id,admin);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @PostMapping("/updatePassword")
    @ApiOperation("修改管理员密码")
    public CommonResult updatePassword(@RequestBody UpdateAdminPasswordParam updateAdminPasswordParam) {
        int status = adminService.updatePassword(updateAdminPasswordParam);
        if (status > 0) {
            return CommonResult.success(status);
        } else {
            switch (status) {
                case -1:
                    return CommonResult.failed("提交参数不合法");
                case -2:
                    return CommonResult.failed("找不到该用户");
                case -3:
                    return CommonResult.failed("旧密码错误");
                default:
                    return CommonResult.failed();
            }
        }
    }

    @PostMapping("/updateStatus/{id}")
    @ApiOperation("修改管理员状态")
    public CommonResult updateStatus(@PathVariable Long id,@RequestParam Integer status){
        UmsAdmin admin = adminService.getItem(id);
        admin.setStatus(status);
        int count = adminService.update(id,admin);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除管理员")
    public CommonResult delete(@PathVariable Long id){
        int count = adminService.delete(id);
        if(count > 0){
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
