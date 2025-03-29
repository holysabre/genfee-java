package com.pange.genfee.portal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.pange.genfee.mapper.*;
import com.pange.genfee.model.*;
import com.pange.genfee.portal.domain.PmsPortalProductDetail;
import com.pange.genfee.portal.domain.PmsProductCategoryNode;
import com.pange.genfee.portal.service.PmsPortalProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/3/29}
 */
@Service
public class PmsPortalProductServiceImpl implements PmsPortalProductService {

    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;

    @Override
    public List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductExample productExample = new PmsProductExample();
        PmsProductExample.Criteria criteria = productExample.createCriteria();
        if(StrUtil.isNotEmpty(keyword)){
            criteria.andNameLike("%"+keyword+"%");
        }
        if(brandId != null){
            criteria.andBrandIdEqualTo(brandId);
        }
        if(productCategoryId != null){
            criteria.andProductCategoryIdEqualTo(productCategoryId);
        }
        //1->按新品；2->按销量；3->价格从低到高；4->价格从高到低
        switch (sort){
            case 1:
                productExample.setOrderByClause("id desc");
                break;
            case 2:
                productExample.setOrderByClause("sale desc");
                break;
            case 3:
                productExample.setOrderByClause("price asc");
                break;
            case 4:
                productExample.setOrderByClause("price desc");
                break;
        }

        return productMapper.selectByExample(productExample);
    }

    @Override
    public List<PmsProductCategoryNode> categoryTreeList() {
        PmsProductCategoryExample productCategoryExample = new PmsProductCategoryExample();
        List<PmsProductCategory> categoryList = productCategoryMapper.selectByExample(productCategoryExample);
        return categoryList.stream().filter(item -> item.getParentId() == 0)
                .map(item -> covert(item,categoryList)).collect(Collectors.toList());
    }

    private PmsProductCategoryNode covert(PmsProductCategory category, List<PmsProductCategory> categoryList){
        PmsProductCategoryNode node = new PmsProductCategoryNode();
        BeanUtil.copyProperties(category,node);
        List<PmsProductCategoryNode> children = categoryList.stream().filter(item -> item.getParentId().equals(category.getId()))
                .map(item -> covert(item,categoryList))
                .collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }

    @Override
    public PmsPortalProductDetail detail(Long id) {
        PmsPortalProductDetail productDetail = new PmsPortalProductDetail();
        PmsProduct product = productMapper.selectByPrimaryKey(id);
        productDetail.setProduct(product);

        PmsBrand brand = brandMapper.selectByPrimaryKey(product.getBrandId());
        productDetail.setBrand(brand);

        PmsProductAttributeExample productAttributeExample = new PmsProductAttributeExample();
        productAttributeExample.createCriteria().andProductAttributeCategoryIdEqualTo(product.getProductAttributeCategoryId());
        List<PmsProductAttribute> attributeList = productAttributeMapper.selectByExample(productAttributeExample);
        productDetail.setProductAttributeList(attributeList);

        PmsProductAttributeValueExample productAttributeValueExample = new PmsProductAttributeValueExample();
        productAttributeValueExample.createCriteria().andProductIdEqualTo(product.getId());
        List<PmsProductAttributeValue> attributeValueList = productAttributeValueMapper.selectByExample(productAttributeValueExample);
        productDetail.setProductAttributeValueList(attributeValueList);

        PmsSkuStockExample skuStockExample = new PmsSkuStockExample();
        skuStockExample.createCriteria().andProductIdEqualTo(product.getId());
        List<PmsSkuStock> skuStockList = skuStockMapper.selectByExample(skuStockExample);
        productDetail.setSkuStockList(skuStockList);

        return productDetail;
    }
}

