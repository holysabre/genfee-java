package com.pange.genfee;

import com.pange.genfee.mapper.UmsMenuMapper;
import com.pange.genfee.model.UmsMenu;
import com.pange.genfee.model.UmsMenuExample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @auther Pange
 * @description
 * @date {2025/3/20}
 */
@SpringBootTest
public class GenfeeAdminApplicationTests {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenfeeAdminApplicationTests.class);

    @Autowired
    private UmsMenuMapper menuMapper;
    private List<UmsMenu> menuList;

    @BeforeEach
    void eachInit(){
        menuList = menuMapper.selectByExample(new UmsMenuExample());
        LOGGER.info("init menuList");
    }

    @Test
    public void filterTest(){
        List<UmsMenu> list = menuList.stream()
                .filter(menu -> menu.getParentId() == 0L)
                .collect(Collectors.toList());
        LOGGER.info("filter: {}",list);
    }

    @Test
    public void mapTest(){
        List<Long> list = menuList.stream()
                .filter(menu -> menu.getParentId() == 0L)
                .map(UmsMenu::getId)
                .collect(Collectors.toList());
        LOGGER.info("filter: {}",list);
    }

    @Test
    public void sortedTest(){
        List<Long> list = menuList.stream()
                .sorted((menu1,menu2) -> {return menu2.getSort().compareTo(menu1.getSort());})
                .map(UmsMenu::getId)
                .collect(Collectors.toList());
        LOGGER.info("filter: {}",list);
    }

    @Test
    public void collection2MapTest(){
        Map<Long,UmsMenu> list = menuList.stream()
                .collect(Collectors.toMap(UmsMenu::getId, menu -> menu));
        LOGGER.info("filter: {}",list);
    }
}
