package com.dreamtech.web.handler;

public class HandlerParam {

    private String paramName;
    private Class<?> paramType;
    private boolean isRequestBody;

    public HandlerParam(String paramName, Class<?> paramType, boolean isRequestBody) {
        this.paramName = paramName;
        this.paramType = paramType;
        this.isRequestBody = isRequestBody;
    }

    public String getParamName() {
        return paramName;
    }

    public Class<?> getParamType() {
        return paramType;
    }

    public boolean isRequestBody() {
        return isRequestBody;
    }
}
