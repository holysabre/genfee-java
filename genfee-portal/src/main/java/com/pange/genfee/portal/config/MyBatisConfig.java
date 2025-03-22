package com.pange.genfee.portal.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis相关配置
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.pange.genfee.mapper","com.pange.genfee.portal.dao"})
public class MyBatisConfig {
}
