package com.pange.genfee.service.impl;

import com.pange.genfee.mapper.UmsResourceMapper;
import com.pange.genfee.model.UmsResource;
import com.pange.genfee.model.UmsResourceExample;
import com.pange.genfee.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/18}
 */
@Service
public class UmsResourceServiceImpl implements UmsResourceService {

    @Autowired
    private UmsResourceMapper resourceMapper;

    @Override
    public List<UmsResource> listAll() {
        return resourceMapper.selectByExample(new UmsResourceExample());
    }
}
