package com.pange.genfee.dto;

import com.pange.genfee.model.UmsMenu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @auther Pange
 * @description
 * @date {2025/3/20}
 */
@Getter
@Setter
public class UmsMenuNode extends UmsMenu {
    @ApiModelProperty("子菜单")
    private List<UmsMenuNode> children;
}
