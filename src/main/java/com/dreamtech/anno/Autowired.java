package com.dreamtech.anno;

import java.lang.annotation.*;

/**
 * 自动绑定Bean
 * 默认去BeanFactory中寻找以 类名（第一个字母变小写） 为名称的bean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Autowired {
    String beanName() default "";
}
