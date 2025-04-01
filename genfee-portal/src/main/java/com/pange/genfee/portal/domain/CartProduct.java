package com.pange.genfee.portal.domain;

import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.model.PmsProductAttribute;
import com.pange.genfee.model.PmsSkuStock;
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
public class CartProduct extends PmsProduct {

    @ApiModelProperty("商品属性列表")
    private List<PmsProductAttribute> attributeList;
    @ApiModelProperty("商品sku库存列表")
    private List<PmsSkuStock> skuStockList;
}
