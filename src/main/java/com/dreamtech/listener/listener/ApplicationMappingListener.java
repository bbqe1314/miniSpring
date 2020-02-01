package com.dreamtech.listener.listener;


import com.dreamtech.anno.Listener;
import com.dreamtech.context.ApplicationContext;
import com.dreamtech.listener.event.EnvironmentPrepareEvent;
import com.dreamtech.web.handler.HandlerManager;

/**
 * 处理controller中的mapping
 */
@Listener
public class ApplicationMappingListener implements ApplicationListener<EnvironmentPrepareEvent> {
    @Override
    public void onApplicationEvent(EnvironmentPrepareEvent applicationEvent) {
        HandlerManager.resolveMappingHandler(ApplicationContext.getInstance().getClassList());
    }
}
