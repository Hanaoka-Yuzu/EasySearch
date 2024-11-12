package com.nju.software.download.advice;

import com.nju.software.common.utils.CodeEnum;
import com.nju.software.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description download模块自定义异常处理
 * @Author wxy
 * @Date 2024/2/19
 **/
@Slf4j
@RestControllerAdvice(basePackages = {"com.nju.software.download.controller"})
public class DownloadExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result handleGlobalException(Exception e) {
        log.error("download模块全局异常，错误码：{}，异常原因：{}，异常类型：{}, 堆栈：{}", CodeEnum.FAIL.getCode(), e.getMessage(), e.getClass(), e.getStackTrace());
        return Result.error();
    }

}
