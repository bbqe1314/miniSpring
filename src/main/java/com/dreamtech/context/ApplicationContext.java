package com.dreamtech.context;

import com.dreamtech.core.ClassScanner;

import java.util.HashMap;
import java.util.List;

/**
 * mini-spring 上下文环境
 */
public class ApplicationContext {

    private HashMap<String, Object> appArgs;
    private List<Class<?>> classList;

    private static class SingletonApplicationContext {
        static final ApplicationContext instance = new ApplicationContext();
    }

    /**
     * 单例模式 获取实例
     */
    public static ApplicationContext getInstance() {
        return SingletonApplicationContext.instance;
    }

    private ApplicationContext() {
    }

    /**
     * 扫描类列表
     */
    public synchronized void scanClasses(Class<?> cls) throws Exception {
        List<Class<?>> classList = ClassScanner.scanClasses(cls);
        if (classList == null)
            classList = ClassScanner.scanClasses();
        else {
            classList.addAll(ClassScanner.scanClasses());
        }
        this.classList = classList;
    }


    public List<Class<?>> getClassList() {
        return classList;
    }

    /**
     * 获取application.properties中设置的参数
     */
    public HashMap<String, Object> getAppArgs() {
        return appArgs;
    }

    public void setAppArgs(HashMap<String, Object> appArgs) {
        this.appArgs = appArgs;
    }

}
