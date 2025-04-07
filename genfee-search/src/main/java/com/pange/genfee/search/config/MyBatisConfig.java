package com.pange.genfee.search.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis相关配置
 */
@Configuration
@MapperScan({"com.pange.genfee.mapper","com.pange.genfee.search.dao"})
public class MyBatisConfig {
}
