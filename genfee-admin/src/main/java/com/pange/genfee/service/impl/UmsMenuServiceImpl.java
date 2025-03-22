package com.pange.genfee.service.impl;

import com.pange.genfee.dto.UmsMenuNode;
import com.pange.genfee.mapper.UmsMenuMapper;
import com.pange.genfee.model.UmsMenu;
import com.pange.genfee.model.UmsMenuExample;
import com.pange.genfee.service.UmsMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/3/20}
 */
@Service
public class UmsMenuServiceImpl implements UmsMenuService {

    @Autowired
    private UmsMenuMapper menuMapper;

    @Override
    public List<UmsMenuNode> treeList() {
        List<UmsMenu> menuList = menuMapper.selectByExample(new UmsMenuExample());
        List<UmsMenuNode> result = menuList.stream()
                .filter(menu -> menu.getParentId() == 0L)
                .map(menu -> covertMenuNode(menu,menuList))
                .collect(Collectors.toList());
        return result;
    }

    private UmsMenuNode covertMenuNode(UmsMenu menu, List<UmsMenu> menuList){
        UmsMenuNode node = new UmsMenuNode();
        BeanUtils.copyProperties(menu,node);
        List<UmsMenuNode> children = menuList.stream()
                .filter(subMenu -> subMenu.getParentId().equals(menu.getId()))
                .map(subMenu -> covertMenuNode(subMenu,menuList))
                .collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }
}
