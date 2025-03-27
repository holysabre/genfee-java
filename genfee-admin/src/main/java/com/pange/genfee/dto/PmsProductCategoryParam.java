package com.pange.genfee.dto;

import com.pange.genfee.validator.FlagValidator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/25}
 */
@Data
@EqualsAndHashCode
public class PmsProductCategoryParam {
    @ApiModelProperty("父分类ID")
    private Long parentId;
    @ApiModelProperty(value = "分类名称",required = true)
    @NotEmpty
    private String name;
    @ApiModelProperty("单位")
    private String productUnit;
    @ApiModelProperty("是否在导航栏显示")
    @FlagValidator(value = {"0","1"}, message = "状态只能为0或1")
    private Integer navStatus;
    @ApiModelProperty("是否显示")
    @FlagValidator(value = {"0","1"}, message = "状态只能为0或1")
    private Integer showStatus;
    @ApiModelProperty("排序")
    @Min(0)
    private Integer sort;
    @ApiModelProperty("图标")
    private String icon;
    @ApiModelProperty("关键字")
    private String keyword;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("产品相关筛选属性集合")
    private List<Long> productAttributeIdList;
}
