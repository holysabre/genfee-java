package com.pange.genfee.config;

import com.pange.genfee.common.config.BaseSwaggerConfig;
import com.pange.genfee.common.domain.SwaggerProperties;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther Pange
 * @description
 * @date {2024/3/20}
 */
@Configuration
public class SwaggerConfig extends BaseSwaggerConfig {
    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.pange.genfee.controller")
                .title("genfee admin system")
                .description("api document for genfee admin system")
                .contactName("Pange")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }

    @Bean
    public BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
        return generateBeanPostProcessor();
    }
}
