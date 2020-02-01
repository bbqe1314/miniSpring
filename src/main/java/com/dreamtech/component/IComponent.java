package com.dreamtech.component;

/**
 * 实现此方法，配合@Component注解可自己实现组件注入
 */
public interface IComponent {
    void init();

    void stop();
}
