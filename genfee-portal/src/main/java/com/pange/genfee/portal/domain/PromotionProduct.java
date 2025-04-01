package com.pange.genfee.portal.domain;

import com.pange.genfee.model.PmsProduct;
import com.pange.genfee.model.PmsProductFullReduction;
import com.pange.genfee.model.PmsProductLadder;
import com.pange.genfee.model.PmsSkuStock;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/31}
 */
@Getter
@Setter
public class PromotionProduct extends PmsProduct {
    @ApiModelProperty("商品库存列表")
    private List<PmsSkuStock> skuStockList;
    @ApiModelProperty("商品打折信息列表")
    private List<PmsProductLadder> productLadderList;
    @ApiModelProperty("商品满减信息列表")
    private List<PmsProductFullReduction> productFullReductionList;
}
