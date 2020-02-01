package com.dreamtech.listener.listener;

import org.apache.catalina.LifecycleException;
import com.dreamtech.anno.Listener;
import com.dreamtech.context.ApplicationContext;
import com.dreamtech.exceptions.MINIExceptionProcessor;
import com.dreamtech.listener.event.EnvironmentStartEvent;
import com.dreamtech.web.server.TomcatServer;


/**
 * 启动tomcat服务器
 */
@Listener
public class ApplicationServerStartListener implements ApplicationListener<EnvironmentStartEvent> {
    @Override
    public void onApplicationEvent(EnvironmentStartEvent applicationEvent) {
        TomcatServer tomcatServer = new TomcatServer(ApplicationContext.getInstance().getAppArgs());
        try {
            tomcatServer.startServer();
        } catch (LifecycleException e) {
            MINIExceptionProcessor.getInstance().putException("start tomcat error", e);
        }
    }
}
