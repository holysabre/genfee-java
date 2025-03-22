package com.pange.genfee.portal.service;

import com.pange.genfee.model.UmsMember;

public interface UmsMemberCacheService {

    /**
     * 设置验证码缓存
     */
    void setAuthCode(String telephone, String authCode);

    /**
     * 获取验证码缓存
     */
    String getAuthCode(String telephone);

    UmsMember getMember(String username);

    void setMember(UmsMember member);
}
