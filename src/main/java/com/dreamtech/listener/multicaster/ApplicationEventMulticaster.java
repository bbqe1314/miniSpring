package com.dreamtech.listener.multicaster;

import com.dreamtech.listener.event.ApplicationEvent;
import com.dreamtech.listener.listener.ApplicationListener;
import com.dreamtech.utils.ApplicationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 广播器
 * 广播监听事件
 */
public class ApplicationEventMulticaster implements EventMulticaster {

    private List<ApplicationListener> listenerList = new ArrayList<>();

    @Override
    public void multicastEvent(ApplicationEvent applicationEvent) {
        for (ApplicationListener listener : listenerList) {
            String listenerInterestedEventName = ApplicationUtil.getSingleGenericNameFromImpl(listener.getClass());
            String currentEventName = applicationEvent.getClass().getName();
            if (listenerInterestedEventName.equals(currentEventName))
                listener.onApplicationEvent(applicationEvent);
        }

    }

    @Override
    public void addListener(ApplicationListener applicationListener) {
        listenerList.add(applicationListener);
    }

    @Override
    public void addListeners(List<ApplicationListener> ApplicationListeners) {
        listenerList.addAll(ApplicationListeners);
    }

    @Override
    public void removeListener(ApplicationListener applicationListener) {
        listenerList.remove(applicationListener);
    }


}
