package com.fj.aux.network.entity;

public class BaseResult<T> {
    private String code;
    private String message;
    private T data;
    private static final String SUCCESS_CODE = "0000";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess(){
        return SUCCESS_CODE.equals(code);
    }
}
