package com.pange.genfee.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/4/7}
 */
@Data
@EqualsAndHashCode
public class EsProductRelatedInfo {
    private List<String> brandNames;
    private List<String> productCategoryNames;
    private List<ProductAttr> productAttrs;

    @Data
    @EqualsAndHashCode
    public static class ProductAttr{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;
    }
}
