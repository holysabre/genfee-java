package com.pange.genfee.dao;

import com.pange.genfee.dto.PmsProductCategoryWithChildrenItem;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/25}
 */
public interface PmsProductCategoryDao {

    //获取商品分类及子集
    List<PmsProductCategoryWithChildrenItem> listWithChildren();
}
