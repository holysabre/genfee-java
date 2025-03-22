package com.pange.genfee.security.annotation;

import java.lang.annotation.*;

/**
 * 自定义缓存异常注解，有该注解的地方会自动抛出异常
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheException {
}
