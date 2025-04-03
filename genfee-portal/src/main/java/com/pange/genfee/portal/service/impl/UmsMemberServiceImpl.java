package com.pange.genfee.portal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.pange.genfee.common.exception.Asserts;
import com.pange.genfee.mapper.UmsMemberLevelMapper;
import com.pange.genfee.mapper.UmsMemberMapper;
import com.pange.genfee.model.UmsMember;
import com.pange.genfee.model.UmsMemberExample;
import com.pange.genfee.model.UmsMemberLevel;
import com.pange.genfee.model.UmsMemberLevelExample;
import com.pange.genfee.portal.domain.MemberDetails;
import com.pange.genfee.portal.domain.MemberRegisterParam;
import com.pange.genfee.portal.service.UmsMemberCacheService;
import com.pange.genfee.portal.service.UmsMemberService;
import com.pange.genfee.security.util.JwtTokenUtil;
import com.pange.genfee.security.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * @auther Pange
 * @description
 * @date {2025/3/19}
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Value("${redis.key.authCode}")
    private String REDIS_KEY_PREFIX_AUTH_CODE;
    @Value("${redis.expire.authCode}")
    private Long AUTH_CODE_EXPIRE_SECONDS;

    @Override
    public void generateAuthCode(String phone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        String authCode = sb.toString();
        LOGGER.info("authCode: {}", authCode);
        getCacheService().setAuthCode(phone,authCode);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UmsMember member = getByUsername(username);
        if(member != null){
            return new MemberDetails(member);
        }
        throw new UsernameNotFoundException("用户名或密码错误");
    }

    @Override
    public UmsMemberCacheService getCacheService() {
        return SpringUtil.getBean(UmsMemberCacheService.class);
    }

    private UmsMember getByUsername(String username){
        UmsMember member = getCacheService().getMember(username);
        if(member !=null){
            return member;
        }
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsMember> memberList = memberMapper.selectByExample(example);
        if(memberList != null && memberList.size() > 0){
            member = memberList.get(0);
            getCacheService().setMember(member);
        }
        return member;
    }

    @Override
    public String login(String username, String password) {
        String token = null;

        try {
            UserDetails memberDetails = loadUserByUsername(username);
            boolean isMatch = passwordEncoder.matches(password, memberDetails.getPassword());
            if(!isMatch){
                throw new BadCredentialsException("密码错误");
            }
            UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(memberDetails,null, memberDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(memberDetails);
        }catch (AuthenticationException e){
            LOGGER.info("登录失败: {}",e.getMessage());
        }

        return token;
    }

    @Override
    public void register(MemberRegisterParam param) {
        if(!verifyAuthCode(param.getAuthCode(),param.getPhone())){
            Asserts.fail("验证码错误");
        }
        UmsMemberExample memberExample = new UmsMemberExample();
        memberExample.createCriteria().andUsernameEqualTo(param.getUsername());
        memberExample.or(memberExample.createCriteria().andPhoneEqualTo(param.getPhone()));
        List<UmsMember> memberList = memberMapper.selectByExample(memberExample);
        if(!CollectionUtil.isEmpty(memberList)){
            Asserts.fail("用户已存在");
        }
        UmsMember member = new UmsMember();
        BeanUtils.copyProperties(param,member);
        member.setPassword(passwordEncoder.encode(param.getPassword()));
        member.setStatus(1);

        UmsMemberLevelExample memberLevelExample = new UmsMemberLevelExample();
        memberLevelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(memberLevelExample);
        if(memberLevelList != null && memberLevelList.size() > 0){
            UmsMemberLevel defaultMemberLevel = memberLevelList.get(0);
            member.setMemberLevelId(defaultMemberLevel.getId());
        }

        memberMapper.insert(member);
    }

    @Override
    public UmsMember getCurrentMember() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        return memberDetails.getUmsMember();
    }

    @Override
    public void updateIntegration(Long id, Integer integration) {
        UmsMember member = memberMapper.selectByPrimaryKey(id);
        member.setHistoryIntegration(member.getIntegration());
        member.setIntegration(integration);
        UmsMemberExample example = new UmsMemberExample();
        memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean verifyAuthCode(String authCode, String phone){
        if(StrUtil.isEmpty(authCode)){
            return false;
        }
        String realAuthCode = getCacheService().getAuthCode(phone);
        if(realAuthCode == null){
            return false;
        }
        return realAuthCode.equals(authCode);
    }

}
