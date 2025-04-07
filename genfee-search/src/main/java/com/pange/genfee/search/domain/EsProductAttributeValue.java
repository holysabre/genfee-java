package com.pange.genfee.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @auther Pange
 * @description
 * @date {2025/4/7}
 */
@Data
@EqualsAndHashCode
public class EsProductAttributeValue implements Serializable {
    private static final long serialVersionID = -1L;
    private Long id;
    private Long productAttributeId;
    @Field(type = FieldType.Keyword)
    private String value;
    private Integer type;
    @Field(type = FieldType.Keyword)
    private String name;
}
