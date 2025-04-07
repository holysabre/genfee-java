package com.pange.genfee.search.dao;

import com.pange.genfee.search.domain.EsProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EsProductDao {

    /**
     * 根据id搜索商品
     */
    List<EsProduct> getAllEsProductList(@Param("id") Long id);
}
