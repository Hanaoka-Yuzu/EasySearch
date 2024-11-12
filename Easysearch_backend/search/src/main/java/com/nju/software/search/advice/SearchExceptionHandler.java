package com.nju.software.search.advice;

import com.nju.software.common.utils.CodeEnum;
import com.nju.software.common.utils.Result;
import com.nju.software.search.exception.SearchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description search 模块自定义异常处理
 * @Author wxy
 * @Date 2024/3/15
 **/
@Slf4j
@RestControllerAdvice(basePackages = {"com.nju.software.search.controller"})
public class SearchExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public Result handleGlobalException(Exception e) {
        e.printStackTrace();
        log.error("搜索模块全局异常，错误码：{}，异常原因：{}，异常类型：{}, 堆栈：{}", CodeEnum.FAIL.getCode(), e.getMessage(), e.getClass(), e.getStackTrace());
        return Result.error();
    }

    @ExceptionHandler(value = SearchException.class)
    public Result handleSearchException(SearchException e) {
        e.printStackTrace();
        log.error("解析xml模块异常，错误码：{}，异常原因：{}，异常类型：{}", e.getCode(), e.getMessage(), e.getClass());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e){
        log.error("解析xml模块异常，错误码：{}，异常原因：{}", CodeEnum.DUPLICATE_KEY_EXCEPTION.getCode(), CodeEnum.DUPLICATE_KEY_EXCEPTION.getMsg());
        return Result.error(CodeEnum.DUPLICATE_KEY_EXCEPTION.getCode(), CodeEnum.DUPLICATE_KEY_EXCEPTION.getMsg());
    }

}
