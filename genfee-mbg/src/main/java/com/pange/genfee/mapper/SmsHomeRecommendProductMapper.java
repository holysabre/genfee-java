package com.pange.genfee.mapper;

import com.pange.genfee.model.SmsHomeRecommendProduct;
import com.pange.genfee.model.SmsHomeRecommendProductExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SmsHomeRecommendProductMapper {
    long countByExample(SmsHomeRecommendProductExample example);

    int deleteByExample(SmsHomeRecommendProductExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsHomeRecommendProduct row);

    int insertSelective(SmsHomeRecommendProduct row);

    List<SmsHomeRecommendProduct> selectByExample(SmsHomeRecommendProductExample example);

    SmsHomeRecommendProduct selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") SmsHomeRecommendProduct row, @Param("example") SmsHomeRecommendProductExample example);

    int updateByExample(@Param("row") SmsHomeRecommendProduct row, @Param("example") SmsHomeRecommendProductExample example);

    int updateByPrimaryKeySelective(SmsHomeRecommendProduct row);

    int updateByPrimaryKey(SmsHomeRecommendProduct row);
}