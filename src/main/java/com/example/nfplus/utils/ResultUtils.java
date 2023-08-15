/*
 * @Description: response返回信息构造类
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 15:33:58
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 11:28:02
 */


package com.example.nfplus.utils;

import java.util.HashMap;
import java.util.Map;

public class ResultUtils {
    private Boolean success;
    private int code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    private ResultUtils() {}

    //请求成功方法
    public static ResultUtils ok(){
        ResultUtils result = new ResultUtils();
        result.setSuccess(true);
        result.setCode(ResultCode.OK);
        result.setMessage("请求成功");
        return result;
    }

    //请求失败方法
    public static ResultUtils error(){
        ResultUtils result = new ResultUtils();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR);
        result.setMessage("请求失败");
        return result;
    }

    //封装返回数据
    public ResultUtils data(String key, Object value){
        this.data.put(key,value);
        return this;
    }

    public ResultUtils data(Map<String, Object> map){
        this.setData(map);
        return this;
    }

    public ResultUtils message(String message){
        this.setMessage(message);
        return this;
    }
}
