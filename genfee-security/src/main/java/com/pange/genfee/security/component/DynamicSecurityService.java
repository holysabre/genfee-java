package com.pange.genfee.security.component;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * @auther Pange
 * @description
 * @date {2024/3/18}
 */
public interface DynamicSecurityService {

    Map<String, ConfigAttribute> loadDataSource();
}
