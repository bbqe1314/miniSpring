package com.dreamtech.web.handler;

import com.dreamtech.web.anno.Controller;
import com.dreamtech.web.anno.RequestBody;
import com.dreamtech.web.anno.RequestMapping;
import com.dreamtech.web.anno.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HandlerManager {


    public static List<MappingHandler> mappingHandlerList = new LinkedList<>();

    public static void resolveMappingHandler(List<Class<?>> classList) {
        classList.forEach(cls -> {
            if (cls.isAnnotationPresent(Controller.class)) {
                parseHandlerFromController(cls);
            }
        });
    }

    private static void parseHandlerFromController(Class<?> controller) {
        Method[] methods = controller.getMethods();
        for (Method method : methods) {
            MappingHandler mappingHandler = parseMethodInController(method);
            if (mappingHandler != null) {
                mappingHandler.setController(controller);
                HandlerManager.mappingHandlerList.add(mappingHandler);
            }
        }
    }


    private static MappingHandler parseMethodInController(Method method) {
        if (!method.isAnnotationPresent(RequestMapping.class)) {
            return null;
        }
        String uri = method.getDeclaredAnnotation(RequestMapping.class).uri();
        String methodType = method.getDeclaredAnnotation(RequestMapping.class).method();
        List<HandlerParam> handlerParamList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                handlerParamList.add(
                        new HandlerParam(
                                parameter.getDeclaredAnnotation(RequestParam.class).value(),
                                parameter.getType(),
                                parameter.isAnnotationPresent(RequestBody.class)
                        ));
            } else {
                handlerParamList.add(
                        new HandlerParam(
                                parameter.getName(),
                                parameter.getType(),
                                parameter.isAnnotationPresent(RequestBody.class)
                        ));
            }
        }

        HandlerParam[] params = handlerParamList.toArray(new HandlerParam[0]);
        return new MappingHandler(uri, method, params, methodType);
    }


}
