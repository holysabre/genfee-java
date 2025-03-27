package com.pange.genfee.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.pange.genfee.dao.PmsProductCategoryAttributeRelationDao;
import com.pange.genfee.dao.PmsProductCategoryDao;
import com.pange.genfee.dto.PmsProductCategoryParam;
import com.pange.genfee.dto.PmsProductCategoryWithChildrenItem;
import com.pange.genfee.mapper.PmsProductCategoryAttributeRelationMapper;
import com.pange.genfee.mapper.PmsProductCategoryMapper;
import com.pange.genfee.mapper.PmsProductMapper;
import com.pange.genfee.model.*;
import com.pange.genfee.service.PmsProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/25}
 */
@Service
public class PmsProductCategoryServiceImpl implements PmsProductCategoryService {

    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private PmsProductCategoryDao productCategoryDao;
    @Autowired
    private PmsProductCategoryAttributeRelationDao productCategoryAttributeRelationDao;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryAttributeRelationMapper productCategoryAttributeRelationMapper;

    @Override
    public List<PmsProductCategory> getList(Long parentId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria().andParentIdEqualTo(parentId);
        return productCategoryMapper.selectByExample(example);
    }

    @Override
    public PmsProductCategory getItem(Long id) {
        return productCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public int create(PmsProductCategoryParam productCategoryParam) {
        PmsProductCategory productCategory = new PmsProductCategory();
        BeanUtil.copyProperties(productCategoryParam,productCategory);
        productCategory.setProductCount(0);
        setCategoryLevel(productCategory);
        int count = productCategoryMapper.insert(productCategory);
        List<Long> attributeIdList = productCategoryParam.getProductAttributeIdList();
        if(CollectionUtil.isNotEmpty(attributeIdList)){
            insertRelationList(productCategory.getId(),attributeIdList);
        }
        return count;
    }

    /**
     * 批量插入商品分类与筛选属性关系表
     * @param productCategoryId 商品分类id
     * @param productAttributeIdList 商品属性id集合
     */
    private void insertRelationList(Long productCategoryId, List<Long> productAttributeIdList){
        List<PmsProductCategoryAttributeRelation> relationList = new ArrayList<>();
        for(Long attributeId : productAttributeIdList){
            PmsProductCategoryAttributeRelation relation = new PmsProductCategoryAttributeRelation();
            relation.setProductCategoryId(productCategoryId);
            relation.setProductAttributeId(attributeId);
            relationList.add(relation);
        }
        productCategoryAttributeRelationDao.insertList(relationList);
    }

    @Override
    public int update(Long id, PmsProductCategoryParam productCategoryParam) {
        PmsProductCategory productCategory = new PmsProductCategory();
        BeanUtil.copyProperties(productCategoryParam,productCategory);
        productCategory.setId(id);
        setCategoryLevel(productCategory);
        //更新商品中的分类名称
        PmsProduct product = new PmsProduct();
        product.setProductCategoryName(productCategory.getName());
        PmsProductExample productExample = new PmsProductExample();
        productExample.createCriteria().andProductCategoryIdEqualTo(productCategory.getId());
        productMapper.updateByExampleSelective(product,productExample);
        //更新筛选属性
        PmsProductCategoryAttributeRelationExample relationExample = new PmsProductCategoryAttributeRelationExample();
        relationExample.createCriteria().andProductCategoryIdEqualTo(productCategory.getId());
        productCategoryAttributeRelationMapper.deleteByExample(relationExample);
        if(CollectionUtil.isNotEmpty(productCategoryParam.getProductAttributeIdList())){
            insertRelationList(productCategory.getId(),productCategoryParam.getProductAttributeIdList());
        }

        return productCategoryMapper.updateByPrimaryKeySelective(productCategory);
    }

    @Override
    public int delete(Long id) {
        return productCategoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateNavStatus(List<Long> ids, Integer navStatus) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setNavStatus(navStatus);
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria().andIdIn(ids);
        return productCategoryMapper.updateByExampleSelective(productCategory,example);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        PmsProductCategory productCategory = new PmsProductCategory();
        productCategory.setShowStatus(showStatus);
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria().andIdIn(ids);
        return productCategoryMapper.updateByExampleSelective(productCategory,example);
    }

    @Override
    public List<PmsProductCategoryWithChildrenItem> listWithChildren() {
        return productCategoryDao.listWithChildren();
    }

    /**
     * 根据分类的parentId设置分类的level
     */
    private void setCategoryLevel(PmsProductCategory productCategory){
        if(productCategory.getParentId() == 0){
            productCategory.setLevel(0);
        }else{
            PmsProductCategory parentCategory = productCategoryMapper.selectByPrimaryKey(productCategory.getParentId());
            if(parentCategory != null){
                productCategory.setLevel(parentCategory.getLevel() + 1);
            }else{
                productCategory.setLevel(0);
            }
        }
    }
}
