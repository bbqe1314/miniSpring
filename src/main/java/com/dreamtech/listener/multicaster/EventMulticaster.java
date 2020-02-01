package com.dreamtech.listener.multicaster;

import com.dreamtech.listener.event.ApplicationEvent;
import com.dreamtech.listener.listener.ApplicationListener;

import java.util.List;

public interface EventMulticaster {

    void multicastEvent(ApplicationEvent weatherEvent);

    void addListener(ApplicationListener ApplicationListener);

    void addListeners(List<ApplicationListener> ApplicationListeners);

    void removeListener(ApplicationListener applicationListener);

}
