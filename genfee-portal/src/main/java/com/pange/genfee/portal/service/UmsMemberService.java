package com.pange.genfee.portal.service;

import com.pange.genfee.model.UmsMember;
import com.pange.genfee.portal.domain.MemberDetails;
import com.pange.genfee.portal.domain.MemberRegisterParam;
import org.springframework.security.core.userdetails.UserDetails;

public interface UmsMemberService {
    void generateAuthCode(String phone);

    boolean verifyAuthCode(String authCode, String phone);

    UserDetails loadUserByUsername(String username);

    UmsMemberCacheService getCacheService();

    String login(String username, String password);

    void register(MemberRegisterParam param);

    //获取当前登录的用户
    UmsMember getCurrentMember();

    //根据会员id修改会员积分
    void updateIntegration(Long id, Integer integration);

    //根据id获取用户
    UmsMember getById(Long id);
}
