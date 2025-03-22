package com.pange.genfee.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @auther Pange
 * @description
 * @date {2025/3/22}
 */
@Getter
@Setter
public class MemberRegisterParam {
    @NotEmpty
    @ApiModelProperty(value = "用户名", required = true)
    private String username;
    @NotEmpty
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @NotEmpty
    @ApiModelProperty(value = "手机号码", required = true)
    private String phone;
    @NotEmpty
    @ApiModelProperty(value = "验证码", required = true)
    private String authCode;
}
