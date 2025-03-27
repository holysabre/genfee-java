package com.pange.genfee.dto;

import com.pange.genfee.model.PmsProductCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/25}
 */
public class PmsProductCategoryWithChildrenItem extends PmsProductCategory {

    @Getter
    @Setter
    @ApiModelProperty("子集分类")
    private List<PmsProductCategory> children;
}
