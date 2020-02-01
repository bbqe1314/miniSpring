package com.dreamtech.anno;

import java.lang.annotation.*;

/**
 * 将整个类作为Bean
 * 默认beanName是 类名（第一个字母变小写）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Bean {
    String beanName() default "";
}
