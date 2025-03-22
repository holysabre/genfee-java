package com.pange.genfee.portal.service;

import com.pange.genfee.portal.domain.MemberDetail;
import com.pange.genfee.portal.domain.MemberRegisterParam;

public interface UmsMemberService {
    void generateAuthCode(String phone);

    boolean verifyAuthCode(String authCode, String phone);

    MemberDetail loadByUsername(String username);

    UmsMemberCacheService getCacheService();

    String login(String username, String password);

    void register(MemberRegisterParam param);
}
