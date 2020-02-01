package com.dreamtech.listener.listener;

import com.dreamtech.listener.event.ApplicationEvent;

public interface ApplicationListener<E extends ApplicationEvent> {
    void onApplicationEvent(E applicationEvent);
}
