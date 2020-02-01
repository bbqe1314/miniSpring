package com.dreamtech.web.handler;


import com.alibaba.fastjson.JSONObject;
import org.apache.http.entity.ContentType;
import com.dreamtech.beans.BeanFactory;
import com.dreamtech.web.anno.ResponseBody;
import com.dreamtech.web.http.HttpCons;
import com.dreamtech.web.http.HttpParamsParseUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class MappingHandler {
    private String uri;
    private Method method;

    private Class<?> controller;
    private HandlerParam[] params;
    private String methodType;


    MappingHandler(String uri, Method method, HandlerParam[] params, String methodType) {
        this.uri = uri;
        this.method = method;
        this.params = params;
        this.methodType = methodType;
    }

    public boolean handle(HttpServletRequest req, HttpServletResponse res) throws Exception {

        if (!uri.equals(req.getRequestURI())) {
            return false;
        }
        if (!methodType.equals(req.getMethod())) {
            res.setStatus(HttpCons.METHOD_NOT_ALLOWED);
            return false;
        }

        for (HandlerParam param : params) {
            if (param.isRequestBody() && params.length > 1) {
                return false;
            }
        }

        Object[] httpParams = HttpParamsParseUtil.parse(req, params);
        Object response = method.invoke(BeanFactory.getBeansByClass(controller).get(0), httpParams);
        PrintWriter writer = res.getWriter();
        if (method.isAnnotationPresent(ResponseBody.class)) {
            res.setContentType(String.valueOf(ContentType.APPLICATION_JSON));
            writer.println(JSONObject.toJSON(response).toString());
        } else {
            writer.println(response);
        }
        return true;
    }

    void setController(Class<?> controller) {
        this.controller = controller;
    }
}
