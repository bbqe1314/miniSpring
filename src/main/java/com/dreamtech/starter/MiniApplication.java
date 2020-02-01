package com.dreamtech.starter;

import com.dreamtech.anno.Component;
import com.dreamtech.anno.Listener;
import com.dreamtech.beans.BeanFactory;
import com.dreamtech.component.IComponent;
import com.dreamtech.context.ApplicationArgs;
import com.dreamtech.context.ApplicationContext;
import com.dreamtech.exceptions.MINIExceptionProcessor;
import com.dreamtech.listener.event.EnvironmentPrepareEvent;
import com.dreamtech.listener.event.EnvironmentStartEvent;
import com.dreamtech.listener.event.EnvironmentSuccessEvent;
import com.dreamtech.listener.listener.ApplicationListener;
import com.dreamtech.listener.multicaster.ApplicationEventMulticaster;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MiniApplication {

    private ApplicationEventMulticaster applicationEventMulticaster;

    /**
     * 1.实例化所有bean
     * 2.获取监听器
     * 3.调用miniSpringStart方法 (监听器实现start事件)
     * 4.调用miniSpringPrepared方法 (将applicationArg中的参数设置对应的组件中[Tomcat&&JDBC])
     * 5.遍历异常Map。如果异常的Map.size ！= 0 就调用miniSpringFailed方法 ([打印异常堆栈&&停止启动 mini-spring&&并关闭已启动的组件[Tomcat&&JDBC]])
     * 6.如果异常的Map.size == 0。调用miniSpringSuccess方法(printBanner)
     */


    private MiniApplication() {

    }

    /**
     * 启动
     */
    public static void run(Class<?> cls, String[] args) {

        String packageName = cls.getPackage().getName();

        new MiniApplication().run(packageName, args);
    }

    private void run(String packageName, String[] args) {
        //初始化
        init(packageName);
        //通知prepare Listener
        miniSpringPrepare();
        //通知start Listener
        miniSpringStart();
        //检查异常
        checkException();
    }

    private void init(String packageName) {
        initLog();//初始化日志（屏蔽tomcat输出）
        initArgs();//初始化参数（获取在application.properties中设置的）
        initClassList(packageName);//初始化类列表
        initBeans();//初始化bean
        initComponent();//初始化组件
        initEventMulticaster();//初始化广播器
    }

    private void initLog() {
        Logger.getLogger("org").setLevel(Level.OFF);
    }

    private void initArgs() {
        HashMap<String, Object> args = null;
        try {
            args = getApplicationArgs();
        } catch (IOException e) {
            MINIExceptionProcessor.getInstance().putException("prepare args error", e);
        }
        ApplicationContext.getInstance().setAppArgs(args);
    }

    private void initClassList(String packageName) {
        try {
            ApplicationContext.getInstance().scanClasses(packageName);
        } catch (Exception e) {
            MINIExceptionProcessor.getInstance().putException("scan classList error", e);
        }
    }

    private void initBeans() {
        List<Class<?>> classList = ApplicationContext.getInstance().getClassList();
        //实例化bean
        try {
            BeanFactory.initBean(classList);
        } catch (Exception e) {
            MINIExceptionProcessor.getInstance().putException("init beans error", e);
        }
    }

    private void initComponent() {
        HashMap<String, Object> components = BeanFactory.getBeans().get(Component.class);
        components.forEach((componentName, component) -> {
            IComponent iComponent = (IComponent) component;
            iComponent.init();
        });
    }

    private void initEventMulticaster() {
        this.applicationEventMulticaster = getApplicationEventMulticaster();
    }


    private HashMap<String, Object> getApplicationArgs() throws IOException {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        HashMap<String, Object> args = getDefaultApplicationArgs();
        Properties properties = new Properties();
        properties.load(is);

        Enumeration<?> en = properties.propertyNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            args.put(key, properties.getProperty(key));
        }
        return args;
    }

    private HashMap<String, Object> getDefaultApplicationArgs() {
        HashMap<String, Object> defaultArgs = new HashMap<>();
        defaultArgs.put(ApplicationArgs.SERVER_PORT, ApplicationArgs.DEFAULT_SERVER_PORT);
        defaultArgs.put(ApplicationArgs.TIME_OUT, ApplicationArgs.DEFAULT_TIME_OUT);
        defaultArgs.put(ApplicationArgs.DATABASE_ADDRESS, ApplicationArgs.DEFAULT_DATABASE_ADDRESS);
        defaultArgs.put(ApplicationArgs.DATABASE_USERNAME, ApplicationArgs.DEFAULT_DATABASE_USERNAME);
        defaultArgs.put(ApplicationArgs.DATABASE_PASSWORD, ApplicationArgs.DEFAULT_DATABASE_PASSWORD);
        defaultArgs.put(ApplicationArgs.MAX_THREADS, ApplicationArgs.DEFAULT_MAX_THREADS);
        defaultArgs.put(ApplicationArgs.DATABASE_PORT, ApplicationArgs.DEFAULT_DATABASE_PORT);
        defaultArgs.put(ApplicationArgs.DATABASE_NAME, ApplicationArgs.DEFAULT_DATABASE_NAME);
        defaultArgs.put(ApplicationArgs.DATABASE_CONFIG, ApplicationArgs.DEFAULT_DATABASE_CONFIG);
        defaultArgs.put(ApplicationArgs.DATABASE_CONNECTION_POOL_MAX_COUNT, ApplicationArgs.DEFAULT_DATABASE_CONNECTION_POOL_MAX_COUNT);
        defaultArgs.put(ApplicationArgs.DATABASE_CONNECTION_POOL_MIN_COUNT, ApplicationArgs.DEFAULT_DATABASE_CONNECTION_POOL_MIN_COUNT);

        defaultArgs.put(ApplicationArgs.OSS_ACCESS_KEY_ID, ApplicationArgs.DEFAULT_OSS_ACCESS_KEY_ID);
        defaultArgs.put(ApplicationArgs.OSS_ACCESS_KEY_SECRET, ApplicationArgs.DEFAULT_OSS_ACCESS_KEY_SECRET);
        defaultArgs.put(ApplicationArgs.OSS_BUCKET_NAME, ApplicationArgs.DEFAULT_OSS_BUCKET_NAME);
        defaultArgs.put(ApplicationArgs.OSS_END_POINT, ApplicationArgs.DEFAULT_OSS_END_POINT);

        return defaultArgs;
    }


    /**
     * 准备阶段
     */
    private void miniSpringPrepare() {
        applicationEventMulticaster.multicastEvent(new EnvironmentPrepareEvent());
    }

    /**
     * 启动阶段
     */
    private void miniSpringStart() {
        applicationEventMulticaster.multicastEvent(new EnvironmentStartEvent());
    }


    /**
     * 成功回调
     */
    private void miniSpringSuccess() {
        applicationEventMulticaster.multicastEvent(new EnvironmentSuccessEvent());
    }

    /**
     * 失败回调
     */
    private void miniSpringFailed() {
        stopComponent();
    }

    private void checkException() {
        HashMap<String, Exception> exceptions = MINIExceptionProcessor.getInstance().getExceptions();
        if (exceptions.size() > 0) {
            miniSpringFailed();//失败
        } else {
            miniSpringSuccess();//成功 打印banner（通知success Listener）
        }
    }

    /**
     * 关闭组件
     */
    private void stopComponent() {
        HashMap<String, Object> components = BeanFactory.getBeans().get(Component.class);
        components.forEach((componentName, component) -> {
            IComponent iComponent = (IComponent) component;
            iComponent.stop();
        });
    }

    /**
     * 获取广播器
     *
     * @return 广播器
     */
    private ApplicationEventMulticaster getApplicationEventMulticaster() {
        ApplicationEventMulticaster applicationEventMulticaster = new ApplicationEventMulticaster();
        List<ApplicationListener> listeners = new ArrayList<>();
        getApplicationListeners().forEach((listenerName, listener) -> {
            listeners.add((ApplicationListener) listener);
        });
        applicationEventMulticaster.addListeners(listeners);
        return applicationEventMulticaster;
    }


    /**
     * 获取所有的事件监听器
     *
     * @return 事件监听器列表
     */
    private HashMap<String, Object> getApplicationListeners() {
        Map<Class<? extends Annotation>, HashMap<String, Object>> allBeans = BeanFactory.getBeans();
        return allBeans.get(Listener.class);
    }


}
