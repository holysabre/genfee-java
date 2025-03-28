package com.pange.genfee.dto;

import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.model.PmsProductAttributeValue;
import com.pange.genfee.model.PmsSkuStock;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/25}
 */
@Data
@EqualsAndHashCode
public class PmsProductParam extends PmsProduct {
    @ApiModelProperty("商品的sku库存信息")
    private List<PmsSkuStock> skuStockList;
    @ApiModelProperty("商品参数及自定义规格属性")
    private List<PmsProductAttributeValue> productAttributeValueList;
}
