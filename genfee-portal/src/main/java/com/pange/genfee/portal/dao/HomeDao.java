package com.pange.genfee.portal.dao;

import com.pange.genfee.model.PmsBrand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HomeDao {
    List<PmsBrand> getRecommendBrandList(@Param("offset") Integer offset, @Param("limit") Integer limit);
}
