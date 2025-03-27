package com.pange.genfee.service;

import com.pange.genfee.dto.PmsProductAttrInfo;
import com.pange.genfee.dto.PmsProductAttributeParam;
import com.pange.genfee.model.PmsProductAttribute;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PmsProductAttributeService {

    /**
     * 根据分类分页获取商品属性列表
     * @param cid
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<PmsProductAttribute> getList(Long cid, Integer type, int pageNum, int pageSize);

    PmsProductAttribute getItem(Long id);

    int create(PmsProductAttributeParam productAttributeParam);

    int update(Long id, PmsProductAttributeParam productAttributeParam);

    @Transactional
    int delete(Long id);

    /**
     * 获取商品分类对应属性列表
     * @param productCategoryId
     * @return
     */
    List<PmsProductAttrInfo> getProductAttrInfo(Long productCategoryId);
}
