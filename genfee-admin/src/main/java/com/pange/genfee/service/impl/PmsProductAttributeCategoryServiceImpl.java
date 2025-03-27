package com.pange.genfee.service.impl;

import com.github.pagehelper.PageHelper;
import com.pange.genfee.dao.PmsProductAttributeCategoryDao;
import com.pange.genfee.dto.PmsProductAttributeCategoryItem;
import com.pange.genfee.mapper.PmsProductAttributeCategoryMapper;
import com.pange.genfee.model.PmsProductAttributeCategory;
import com.pange.genfee.model.PmsProductAttributeCategoryExample;
import com.pange.genfee.service.PmsProductAttributeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/27}
 */
@Service
public class PmsProductAttributeCategoryServiceImpl implements PmsProductAttributeCategoryService {
    @Autowired
    private PmsProductAttributeCategoryMapper productAttributeCategoryMapper;
    @Autowired
    private PmsProductAttributeCategoryDao productAttributeCategoryDao;

    @Override
    public List<PmsProductAttributeCategory> getList(int pageNum, int pangeSize) {
        PageHelper.startPage(pageNum,pangeSize);
        return productAttributeCategoryMapper.selectByExample(new PmsProductAttributeCategoryExample());
    }

    @Override
    public PmsProductAttributeCategory getItem(Long id) {
        return productAttributeCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public int create(String name) {
        PmsProductAttributeCategory productAttributeCategory = new PmsProductAttributeCategoryItem();
        productAttributeCategory.setName(name);
        return productAttributeCategoryMapper.insert(productAttributeCategory);
    }

    @Override
    public int update(Long id, String name) {
        PmsProductAttributeCategory productAttributeCategory = new PmsProductAttributeCategoryItem();
        productAttributeCategory.setId(id);
        productAttributeCategory.setName(name);
        return productAttributeCategoryMapper.updateByPrimaryKeySelective(productAttributeCategory);
    }

    @Override
    public int delete(Long id) {
        return productAttributeCategoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<PmsProductAttributeCategoryItem> getListWithAttribute() {
        return productAttributeCategoryDao.getListWithAttr();
    }
}
