package com.dreamtech.beans;

import org.apache.commons.lang3.StringUtils;
import com.dreamtech.anno.*;
import com.dreamtech.utils.ApplicationUtil;
import com.dreamtech.web.anno.Controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * bean工厂
 * 用于创建整个project所需的所有bean
 */
public class BeanFactory {
    private static Map<Class<? extends Annotation>, HashMap<String, Object>> beans = new ConcurrentHashMap<>();

    /**
     * 感兴趣的注解
     */
    private final static List<Class<? extends Annotation>> interestAnnos = new ArrayList<Class<? extends Annotation>>() {{
        add(Bean.class);
        add(Controller.class);
        add(Listener.class);
        add(Component.class);
        add(Configuration.class);
    }};

    static {
        interestAnnos.forEach(it -> {
            beans.put(it, new HashMap<>());
        });
    }

    /**
     * 获取所有的bean（不按注解类别分类）
     */
    private static HashMap<String, Object> getAllBeans() {
        HashMap<String, Object> allBeans = new HashMap<>();
        beans.forEach((key, value) -> {
            allBeans.putAll(beans.get(key));
        });
        return allBeans;
    }

    /**
     * 通过类来获取该类所有的bean
     *
     * @param cls 类
     * @return 该类所有的bean
     */
    public static ArrayList getBeansByClass(Class<?> cls) {
        ArrayList arrayBeans = new ArrayList();

        for (Annotation anno : cls.getAnnotations()) {
            if (interestAnnos.contains(anno.annotationType())) {
                beans.get(anno.annotationType()).forEach((beanName, instance) -> {
                    if (instance.getClass() == cls)
                        arrayBeans.add(instance);
                });
            }
        }
        return arrayBeans;
    }

    /**
     * 通过beanName获取对应的bean
     *
     * @param beanName beanName
     * @return instance
     */
    public static Object getBeansByName(String beanName) {
        AtomicReference<Object> instance = new AtomicReference<>();
        getAllBeans().forEach((key, value) -> {
            if (key.equals(beanName))
                instance.set(value);
        });

        return instance.get();
    }

    /**
     * 核心方法 初始化bean
     *
     * @param classList 类扫描器扫描到的类列表
     * @throws Exception 循环引用异常。 及A类 使用@Autowired引用B B同样以此方式引用A
     */
    public static synchronized void initBean(List<Class<?>> classList) throws Exception {
        List<Class<?>> toCreate = new ArrayList<>(classList);
        while (toCreate.size() != 0) {
            int remainSize = toCreate.size();
            for (int i = 0; i < toCreate.size(); i++) {
                if (finishCreate(toCreate.get(i))) {
                    toCreate.remove(i);
                }
            }
            if (toCreate.size() == remainSize) {
                throw new Exception("cycle dependency");
            }
        }
    }

    /**
     * 判断一个类是否可以完成bean注入
     *
     * @param cls 目标类
     * @return 该类是否可以完成bean注入
     * @throws Exception invoke创建实例异常
     */
    private static boolean finishCreate(Class<?> cls) throws Exception {

        AtomicBoolean hasInterested = new AtomicBoolean(false);
        AtomicReference<Class<? extends Annotation>> interestAnno = new AtomicReference<>();
        // 该类是否有感兴趣注解
        interestAnnos.forEach(it -> {
            if (cls.isAnnotationPresent(it)) {
                interestAnno.set(it);
                hasInterested.set(true);
            }
        });

        if (!hasInterested.get()) {
            return true;
        }

        //如果是Configuration注解，只扫描其方法中@CustomizeBean想要注入的bean。该类本身并不作为bean注入
        if (interestAnno.get() == Configuration.class) {
            initConfigurationBean(cls);
            return true;
        }


        //获取beanName 如果@Bean有指定，则按照Bean注解指定的beanName。未指定的情况下按照 类名（第一个字母变小写）
        String beanName = getBeanName(interestAnno, cls);


        Object bean = cls.newInstance();
        //处理@Autowired
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                Autowired autowired = field.getAnnotation(Autowired.class);
                String filedBeanName = autowired.beanName();
                if (StringUtils.isBlank(filedBeanName))
                    filedBeanName = ApplicationUtil.firstToLower(field.getType().getSimpleName());
                Object reliantBean = getBeansByName(filedBeanName);
                if (reliantBean == null) {
                    return false;
                }
                field.setAccessible(true);
                field.set(bean, reliantBean);
            }
        }
        //将bean放入beans中
        beans.get(interestAnno.get()).put(beanName, bean);
        return true;
    }

    private static String getBeanName(AtomicReference<Class<? extends Annotation>> interestAnno, Class<?> cls) {
        String beanName = "";
        if (interestAnno.get() == Bean.class)
            beanName = cls.getAnnotation(Bean.class).beanName();
        if (StringUtils.isBlank(beanName))
            beanName = ApplicationUtil.firstToLower(cls.getSimpleName());
        return beanName;
    }

    private static void initConfigurationBean(Class<?> cls) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Method[] methods = cls.getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(CustomizeBean.class)) {
                String beanName = method.getDeclaredAnnotation(CustomizeBean.class).beanName();
                Object customizeBean = method.invoke(cls.newInstance());
                beans.get(Configuration.class).put(beanName, customizeBean);
            }
        }
    }

    /**
     * 对外提供的获取beans的接口
     *
     * @return beans
     */
    public static Map<Class<? extends Annotation>, HashMap<String, Object>> getBeans() {
        return beans;
    }
}
