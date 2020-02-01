package com.dreamtech.web.response;


public class MiniSpringResponse {

    public static final String UNKNOWN_ERROR = "unknown error";

    private String time;

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;


    public static MiniSpringResponse buildOk(Object data) {
        return new MiniSpringResponse(data);
    }


    public static MiniSpringResponse buildError(int errorCode, String msg) {
        return new MiniSpringResponse(errorCode, msg);
    }

    public static MiniSpringResponse buildError(String msg) {
        return new MiniSpringResponse(500, msg);
    }

    private MiniSpringResponse(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private MiniSpringResponse(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    private MiniSpringResponse(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
