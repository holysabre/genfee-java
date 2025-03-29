package com.pange.genfee.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.pange.genfee.common.api.CommonPage;
import com.pange.genfee.mapper.PmsBrandMapper;
import com.pange.genfee.mapper.PmsProductMapper;
import com.pange.genfee.model.PmsBrand;
import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.model.PmsProductExample;
import com.pange.genfee.portal.dao.HomeDao;
import com.pange.genfee.portal.service.PmsPortalBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/28}
 */
@Service
public class PmsPortalBrandServiceImpl implements PmsPortalBrandService {

    @Autowired
    private HomeDao homeDao;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> recommandList(Integer pageNum, Integer pageSize) {
        return homeDao.getRecommendBrandList(pageNum,pageSize);
    }

    @Override
    public PmsBrand detail(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria().andBrandIdEqualTo(brandId).andDeleteStatusEqualTo(0).andPublishStatusEqualTo(1);
        List<PmsProduct> productList = productMapper.selectByExample(productExample);
        return CommonPage.restPage(productList);
    }
}
