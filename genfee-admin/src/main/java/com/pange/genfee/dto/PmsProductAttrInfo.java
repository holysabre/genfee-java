package com.pange.genfee.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @auther Pange
 * @description
 * @date {2025/3/27}
 * 商品分类对应属性分类
 */
@Data
@EqualsAndHashCode
public class PmsProductAttrInfo {
    @ApiModelProperty("商品属性ID")
    private Long attributeId;
    @ApiModelProperty("商品分类属性ID")
    private Long attributeCategoryId;
}
