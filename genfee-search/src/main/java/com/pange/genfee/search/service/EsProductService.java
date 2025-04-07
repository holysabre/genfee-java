package com.pange.genfee.search.service;

import com.pange.genfee.search.domain.EsProduct;
import com.pange.genfee.search.domain.EsProductRelatedInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 搜索商品管理
 */
public interface EsProductService {
    /**
     * 导入商品
     */
    int importAll();

    /**
     * 根据id删除商品
     */
    void delete(Long id);

    /**
     * 根据id创建商品
     */
    EsProduct create(Long id);

    /**
     * 批量删除商品
     */
    void delete(List<Long> ids);

    /**
     * 根据关键词搜索名称或副标题
     */
    Page<EsProduct> search(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 根据关键词复合查询
     */
    Page<EsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum, Integer pageSize, Integer sort);

    /**
     * 根据商品id推荐相关商品
     */
    Page<EsProduct> recommend(Long id, Integer pageNum, Integer pageSize);

    /**
     * 获取搜索词相关品牌、分类、属性
     */
    EsProductRelatedInfo searchRelatedInfo(String keyword);
}
