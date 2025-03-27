package com.pange.genfee.dao;

import com.pange.genfee.model.PmsProductCategoryAttributeRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PmsProductCategoryAttributeRelationDao {
    void insertList(@Param("list") List<PmsProductCategoryAttributeRelation> list);
}
