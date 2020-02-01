package com.dreamtech.anno;

import java.lang.annotation.*;

/**
 * 注册称为监听器
 * 还需实现 ApplicationListener<E extends ApplicationEvent>接口
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Listener {
}
