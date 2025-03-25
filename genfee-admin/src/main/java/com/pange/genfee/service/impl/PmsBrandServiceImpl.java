package com.pange.genfee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.pange.genfee.dto.PmsBrandParam;
import com.pange.genfee.mapper.PmsBrandMapper;
import com.pange.genfee.mapper.PmsProductMapper;
import com.pange.genfee.model.PmsBrand;
import com.pange.genfee.model.PmsBrandExample;
import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.model.PmsProductExample;
import com.pange.genfee.service.PmsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/22}
 */
@Service
public class PmsBrandServiceImpl implements PmsBrandService {
    @Autowired
    private PmsBrandMapper brandMapper;

    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> listAll() {
        return brandMapper.selectByExample(new PmsBrandExample());
    }

    @Override
    public List<PmsBrand> list(String keyword, Integer showStatus, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsBrandExample brandExample = new PmsBrandExample();
        brandExample.setOrderByClause("sort desc");
        PmsBrandExample.Criteria criteria = brandExample.createCriteria();
        if(StrUtil.isNotEmpty(keyword)){
            criteria.andNameLike("%"+keyword+"%");
        }
        if(showStatus !=null){
            criteria.andShowStatusEqualTo(showStatus);
        }
        return brandMapper.selectByExample(brandExample);
    }

    @Override
    public int create(PmsBrandParam brandParam) {
        PmsBrand brand = new PmsBrand();
        BeanUtil.copyProperties(brandParam,brand);
        return brandMapper.insert(brand);
    }

    @Override
    public int update(Long id, PmsBrandParam brandParam) {
        PmsBrand brand = new PmsBrand();
        BeanUtil.copyProperties(brandParam,brand);
        brand.setId(id);
        brand.setFirstLetter(brand.getName().substring(0,1));
        //更新产品的品牌名称
        PmsProduct product = new PmsProduct();
        product.setBrandName(brand.getName());
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria().andBrandIdEqualTo(brand.getId());
        productMapper.updateByExample(product,productExample);
        return brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public int delete(Long id) {
        return brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int delete(List<Long> ids) {
        PmsBrandExample brandExample = new PmsBrandExample();
        brandExample.createCriteria().andIdIn(ids);
        return brandMapper.deleteByExample(brandExample);
    }

    @Override
    public PmsBrand getItem(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateShowStatus(List<Long> ids, int showStatus) {
        PmsBrand brand = new PmsBrand();
        brand.setShowStatus(showStatus);
        PmsBrandExample brandExample = new PmsBrandExample();
        brandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExample(brand,brandExample);
    }

    @Override
    public int updateFactoryStatus(List<Long> ids, int factoryStatus) {
        PmsBrand brand = new PmsBrand();
        brand.setFactoryStatus(factoryStatus);
        PmsBrandExample brandExample = new PmsBrandExample();
        brandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExample(brand,brandExample);
    }
}
