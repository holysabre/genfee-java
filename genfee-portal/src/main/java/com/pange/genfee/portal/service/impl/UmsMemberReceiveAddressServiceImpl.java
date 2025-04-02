package com.pange.genfee.portal.service.impl;

import com.pange.genfee.mapper.UmsMemberReceiveAddressMapper;
import com.pange.genfee.model.UmsMember;
import com.pange.genfee.model.UmsMemberReceiveAddress;
import com.pange.genfee.model.UmsMemberReceiveAddressExample;
import com.pange.genfee.portal.service.UmsMemberReceiveAddressService;
import com.pange.genfee.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/4/1}
 */
@Service
public class UmsMemberReceiveAddressServiceImpl implements UmsMemberReceiveAddressService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private UmsMemberReceiveAddressMapper memberReceiveAddressMapper;

    @Override
    public List<UmsMemberReceiveAddress> list() {
        UmsMember currentMember = memberService.getCurrentMember();
        UmsMemberReceiveAddressExample example = new UmsMemberReceiveAddressExample();
        example.createCriteria().andMemberIdEqualTo(currentMember.getId());
        return memberReceiveAddressMapper.selectByExample(example);
    }
}
