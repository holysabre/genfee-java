package com.pange.genfee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.pange.genfee.dao.PmsProductAttributeDao;
import com.pange.genfee.dto.PmsProductAttrInfo;
import com.pange.genfee.dto.PmsProductAttributeParam;
import com.pange.genfee.mapper.PmsProductAttributeCategoryMapper;
import com.pange.genfee.mapper.PmsProductAttributeMapper;
import com.pange.genfee.model.PmsProductAttribute;
import com.pange.genfee.model.PmsProductAttributeCategory;
import com.pange.genfee.model.PmsProductAttributeExample;
import com.pange.genfee.service.PmsProductAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/27}
 */
@Service
public class PmsProductAttributeServiceImpl implements PmsProductAttributeService {
    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;
    @Autowired
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;
    @Autowired
    private PmsProductAttributeDao productAttributeDao;

    @Override
    public List<PmsProductAttribute> getList(Long cid, Integer type, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductAttributeExample productAttributeExample = new PmsProductAttributeExample();
        productAttributeExample.createCriteria().andProductAttributeCategoryIdEqualTo(cid).andTypeEqualTo(type);
        return productAttributeMapper.selectByExample(productAttributeExample);
    }

    @Override
    public PmsProductAttribute getItem(Long id) {
        return productAttributeMapper.selectByPrimaryKey(id);
    }

    @Override
    public int create(PmsProductAttributeParam productAttributeParam) {
        PmsProductAttribute productAttribute = new PmsProductAttribute();
        BeanUtil.copyProperties(productAttributeParam,productAttribute);
        int count = productAttributeMapper.insert(productAttribute);
        PmsProductAttributeCategory productAttributeCategory = productAttributeCategoryMapper.selectByPrimaryKey(productAttribute.getProductAttributeCategoryId());
        if(productAttribute.getType() == 0){
            productAttributeCategory.setAttributeCount(productAttributeCategory.getAttributeCount() + 1);
        } else if (productAttribute.getType() == 1) {
            productAttributeCategory.setParamCount(productAttributeCategory.getParamCount() + 1);
        }
        productAttributeCategoryMapper.updateByPrimaryKeySelective(productAttributeCategory);
        return count;
    }

    @Override
    public int update(Long id, PmsProductAttributeParam productAttributeParam) {
        PmsProductAttribute productAttribute = new PmsProductAttribute();
        BeanUtil.copyProperties(productAttributeParam,productAttribute);
        productAttribute.setId(id);
        return productAttributeMapper.updateByPrimaryKeySelective(productAttribute);
    }

    @Override
    public int delete(Long id) {
        PmsProductAttribute productAttribute = productAttributeMapper.selectByPrimaryKey(id);
        PmsProductAttributeCategory productAttributeCategory = productAttributeCategoryMapper.selectByPrimaryKey(productAttribute.getProductAttributeCategoryId());
        if(productAttribute.getType() == 0){
            if(productAttributeCategory.getAttributeCount() > 0){
                productAttributeCategory.setAttributeCount(productAttributeCategory.getAttributeCount() - 1);
            }else{
                productAttributeCategory.setAttributeCount(0);
            }
        } else if (productAttribute.getType() == 1) {
            if(productAttributeCategory.getParamCount() > 0){
                productAttributeCategory.setParamCount(productAttributeCategory.getParamCount() - 1);
            }else{
                productAttributeCategory.setParamCount(0);
            }
        }
        return productAttributeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<PmsProductAttrInfo> getProductAttrInfo(Long productCategoryId) {
        return productAttributeDao.getProductAttrInfo(productCategoryId);
    }
}

