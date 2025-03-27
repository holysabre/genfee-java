package com.pange.genfee.dto;

import com.pange.genfee.model.PmsProductAttribute;
import com.pange.genfee.model.PmsProductAttributeCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/27}
 */
public class PmsProductAttributeCategoryItem extends PmsProductAttributeCategory {

    @Getter
    @Setter
    @ApiModelProperty("商品属性列表")
    private List<PmsProductAttribute> productAttributeList;
}
