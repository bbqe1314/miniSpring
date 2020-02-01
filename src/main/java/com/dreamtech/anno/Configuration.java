package com.dreamtech.anno;

import java.lang.annotation.*;

/**
 * 将类注册成配置类
 * 目前1.0只支持：在自己注册bean时，类上加上此注解。再配合@CustomizeBean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {
}
