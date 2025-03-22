package com.pange.genfee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.pange.genfee.dto.PmsBrandParam;
import com.pange.genfee.mapper.PmsBrandMapper;
import com.pange.genfee.model.PmsBrand;
import com.pange.genfee.service.PmsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @auther Pange
 * @description
 * @date {2025/3/22}
 */
@Service
public class PmsBrandServiceImpl implements PmsBrandService {
    @Autowired
    private PmsBrandMapper brandMapper;

    @Override
    public int create(PmsBrandParam brandParam) {
        PmsBrand brand = new PmsBrand();
        BeanUtil.copyProperties(brandParam,brand);
        return brandMapper.insert(brand);
    }
}
