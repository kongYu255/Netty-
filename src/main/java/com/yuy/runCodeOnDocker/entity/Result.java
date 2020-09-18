package com.yuy.runCodeOnDocker.entity;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 响应Rest请求
 */
public class Result {

    // 成功与否
    private boolean success;

    // 信息
    private String message;

    // 数据
    private Map<String, Object> data = new HashMap<String, Object>();


    private Result(){}

    /**
     * 响应成功
     * @param message
     * @return
     */
    public static Result OK(String message) {
        Result result = new Result();
        result.success = true;
        result.message = message;
        return result;
    }

    /**
     * 响应失败
     * @param message
     * @return
     */
    public static Result ERROR(String message) {
        Result result = new Result();
        result.success = false;
        result.message = message;
        return result;
    }

    /**
     * 填充数据
     * @param key
     * @param value
     * @return
     */
    public Result data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public String toJson() {
        String s = JSON.toJSONString(this);
        return s;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
