package com.pange.genfee.portal.config;

import com.pange.genfee.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @auther Pange
 * @description
 * @date {2024/3/19}
 */
@Configuration
public class GenfeeSecurityConfig {

    @Autowired
    private UmsMemberService memberService;

    @Bean
    public UserDetailsService userDetailsService(){
        //获取登录用户信息
        return username -> memberService.loadByUsername(username);
    }

}
