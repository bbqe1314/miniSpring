package com.dreamtech.web.servlet;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.entity.ContentType;
import com.dreamtech.utils.ApplicationUtil;
import com.dreamtech.web.handler.HandlerManager;
import com.dreamtech.web.handler.MappingHandler;
import com.dreamtech.web.http.HttpCons;
import com.dreamtech.web.response.MiniSpringResponse;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class DispatcherServlet implements Servlet {
    @Override
    public void init(ServletConfig config) {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        boolean isHandle = false;
        try {
            for (MappingHandler mappingHandler : HandlerManager.mappingHandlerList) {
                if (mappingHandler.handle(request, response)) {
                    isHandle = true;
                    break;
                }

                if (response.getStatus() != HttpCons.OK) {
                    break;
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            response.setStatus(HttpCons.STATUS_SERVER_ERROR);
        }
        doResponse(isHandle, response);
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }

    private void doResponse(boolean isHandle, HttpServletResponse response) throws IOException {

        if (response.getStatus() != HttpCons.OK) {
            response.setContentType(String.valueOf(ContentType.APPLICATION_JSON));// 那就说明有问题
        } else if (!isHandle) {
            response.setStatus(HttpCons.NOT_FOUND);
            response.setContentType(String.valueOf(ContentType.APPLICATION_JSON));
        } else {
            return;
        }


        PrintWriter printWriter = response.getWriter();
        MiniSpringResponse miniSpringResponse = null;
        switch (response.getStatus()) {
            case 404:
                miniSpringResponse = MiniSpringResponse.buildError(HttpCons.NOT_FOUND, HttpCons.NOT_FOUND_MSG);
                break;
            case 405:
                miniSpringResponse = MiniSpringResponse.buildError(HttpCons.METHOD_NOT_ALLOWED, HttpCons.METHOD_NOT_ALLOWED_MSG);
                break;
            case 500:
                miniSpringResponse = MiniSpringResponse.buildError(HttpCons.STATUS_SERVER_ERROR, HttpCons.STATUS_SERVER_ERROR_MSG);
                break;
        }
        miniSpringResponse = miniSpringResponse == null ? MiniSpringResponse.buildError(MiniSpringResponse.UNKNOWN_ERROR) : miniSpringResponse;
        miniSpringResponse.setTime(ApplicationUtil.currentTime());
        printWriter.println(JSONObject.toJSON(miniSpringResponse));
    }
}
