package com.nju.software.common.utils;


import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 统一返回结果封装类
 * @Author wxy
 * @Date 2024/1/30
 **/
@Data
public class Result {
    private Boolean success;

    private Integer code;

    private String message;

    private Map<String,Object> data = new HashMap<>();

    private Result(){}

    public static Result ok(){
        Result result = new Result();
        result.success = true;
        result.code = CodeEnum.SUCCESS.getCode();
        result.message = CodeEnum.SUCCESS.getMsg();
        return result;
    }

    public static Result error(){
        Result resultEntity = new Result();
        resultEntity.success = false;
        resultEntity.code = CodeEnum.FAIL.getCode();
        resultEntity.message = CodeEnum.FAIL.getMsg();
        return resultEntity;
    }

    public static Result error(Integer code, String message) {
        Result resultEntity = new Result();
        resultEntity.success = false;
        resultEntity.code = code;
        resultEntity.message = message;
        return resultEntity;
    }

    public static Result error(String message) {
        Result resultEntity = new Result();
        resultEntity.success = false;
        resultEntity.code = CodeEnum.FAIL.getCode();
        resultEntity.message = message;
        return resultEntity;
    }

    public static Result error(CodeEnum codeEnum){
        Result resultEntity=new Result();
        resultEntity.success = false;
        resultEntity.code = codeEnum.getCode();
        resultEntity.message = codeEnum.getMsg();
        return resultEntity;
    }

    public static Result error(CodeEnum codeEnum,String msg){
        Result resultEntity=new Result();
        resultEntity.success = false;
        resultEntity.code = codeEnum.getCode();
        resultEntity.message = msg;
        return resultEntity;
    }

    public Result code(Integer code){
        this.setCode(code);
        return this;
    }

    public Result message(String message){
        this.setMessage(message);
        return this;
    }

    public Result data(String key,Object obj){
        this.getData().put(key,obj);
        return this;
    }

    public Result data(Map<String,Object> map){
        this.setData(map);
        return this;
    }

}
