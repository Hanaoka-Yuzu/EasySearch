package com.nju.software.search.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description search 模块自定义异常
 * @Author wxy
 * @Date 2024/3/15
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -5108900647836074949L;

    private String message;
    private Integer code;

    public SearchException(String message, Integer code)
    {
        super(message);
        this.message = message;
        this.code = code;
    }
}
