package com.pange.genfee.portal.domain;

import com.pange.genfee.model.PmsProductCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/29}
 */
@Getter
@Setter
public class PmsProductCategoryNode extends PmsProductCategory {

    @ApiModelProperty("子分类合集")
    private List<PmsProductCategoryNode> children;
}
