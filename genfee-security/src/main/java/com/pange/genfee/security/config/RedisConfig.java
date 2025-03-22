package com.pange.genfee.security.config;

import com.pange.genfee.common.config.BaseRedisConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * @auther Pange
 * @description
 * @date {2024/3/18}
 */
@Configuration
@EnableCaching
public class RedisConfig extends BaseRedisConfig {
}
