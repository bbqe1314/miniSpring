package com.dreamtech.listener.listener;

import com.dreamtech.listener.event.ApplicationEvent;

/**
 * 扩展项 1.0暂不支持
 */
public interface SmartApplicationListener extends ApplicationListener {
    boolean supportsEventType(Class<? extends ApplicationEvent> var1);
}
