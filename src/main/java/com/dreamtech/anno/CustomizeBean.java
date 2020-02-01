package com.dreamtech.anno;

import java.lang.annotation.*;

/**
 * 自定义bean
 * 配合@Configuration使用
 * 必须指定bean名称
 * 且使用该注解的Method不能具有参数
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CustomizeBean {
    String beanName();
}
