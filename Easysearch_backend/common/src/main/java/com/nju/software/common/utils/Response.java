package com.nju.software.common.utils;

import io.grpc.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private Integer code; //状态码
    private String msg;
    private Object result;

    public Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Response buildSuccess(Object result) {
        return new Response(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), result);
    }

    public static Response buildSuccess() {
        return new Response(CodeEnum.SUCCESS.getCode(), CodeEnum.SUCCESS.getMsg(), "操作成功");
    }

    public static Response buildFailed(Integer code, RuntimeException e) {
        return new Response(code, e.getLocalizedMessage(), null);
    }

    public static Response buildFailed(Integer code, String msg) {
        return new Response(code, msg, null);
    }

    public static Response buildFailed(Integer code, Map<String, String> data) {
        return new Response(code, CodeEnum.FAIL.getMsg(), data);
    }
}