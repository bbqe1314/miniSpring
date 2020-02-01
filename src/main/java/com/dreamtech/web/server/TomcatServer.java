package com.dreamtech.web.server;


import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import com.dreamtech.context.ApplicationArgs;
import com.dreamtech.web.servlet.DispatcherServlet;

import java.util.HashMap;

public class TomcatServer {
    private Tomcat tomcat;
    private HashMap<String, Object> appArgs;

    public TomcatServer(HashMap<String, Object> appArgs) {
        this.appArgs = appArgs;
    }

    public void startServer() throws LifecycleException {

        tomcat = new Tomcat();
        tomcat.setPort(Integer.parseInt(appArgs.get(ApplicationArgs.SERVER_PORT).toString()));
        tomcat.start();


        Context context = new StandardContext();
        context.setPath("");
        context.addLifecycleListener(new Tomcat.FixContextListener());
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet).setAsyncSupported(true);
        context.addServletMappingDecoded("/", "dispatcherServlet");
        tomcat.getHost().addChild(context);

        Thread awaitThread = new Thread("tomcat_await") {
            @Override
            public void run() {
                TomcatServer.this.tomcat.getServer().await();
            }
        };
        awaitThread.setDaemon(false);
        awaitThread.start();
    }

    public void stopServer() throws LifecycleException {
        tomcat.stop();
    }
}
