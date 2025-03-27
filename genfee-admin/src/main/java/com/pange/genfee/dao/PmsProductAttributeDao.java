package com.pange.genfee.dao;

import com.pange.genfee.dto.PmsProductAttrInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsProductAttributeDao {

    List<PmsProductAttrInfo> getProductAttrInfo(@Param("product_category_id") Long productCategoryId);
}
