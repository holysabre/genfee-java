package com.pange.genfee.mapper;

import com.pange.genfee.model.PmsProductLadder;
import com.pange.genfee.model.PmsProductLadderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PmsProductLadderMapper {
    long countByExample(PmsProductLadderExample example);

    int deleteByExample(PmsProductLadderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsProductLadder row);

    int insertSelective(PmsProductLadder row);

    List<PmsProductLadder> selectByExample(PmsProductLadderExample example);

    PmsProductLadder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") PmsProductLadder row, @Param("example") PmsProductLadderExample example);

    int updateByExample(@Param("row") PmsProductLadder row, @Param("example") PmsProductLadderExample example);

    int updateByPrimaryKeySelective(PmsProductLadder row);

    int updateByPrimaryKey(PmsProductLadder row);
}