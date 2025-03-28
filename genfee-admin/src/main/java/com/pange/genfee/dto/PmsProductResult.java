package com.pange.genfee.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @auther Pange
 * @description
 * @date {2025/3/27}
 */
public class PmsProductResult extends PmsProductParam{

    @Getter
    @Setter
    @ApiModelProperty("商品所选分类的父id")
    private Long cateParentId;
}
