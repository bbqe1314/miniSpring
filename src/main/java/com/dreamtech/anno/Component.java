package com.dreamtech.anno;

import java.lang.annotation.*;

/**
 * 可自己注册成组件。
 * 需要继承IComponent接口 并加上此注解。
 * 注册后可以在其他类中使用@Autowired注解绑定
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {

}
