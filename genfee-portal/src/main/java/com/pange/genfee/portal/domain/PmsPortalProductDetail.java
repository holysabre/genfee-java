package com.pange.genfee.portal.domain;

import com.pange.genfee.model.*;
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
public class PmsPortalProductDetail {
    @ApiModelProperty("商品信息")
    private PmsProduct product;
    @ApiModelProperty("商品品牌")
    private PmsBrand brand;
    @ApiModelProperty("商品属性列表")
    private List<PmsProductAttribute> productAttributeList;
    @ApiModelProperty("商品属性参数")
    private List<PmsProductAttributeValue> productAttributeValueList;
    @ApiModelProperty("sku库存列表")
    private List<PmsSkuStock> skuStockList;
}
