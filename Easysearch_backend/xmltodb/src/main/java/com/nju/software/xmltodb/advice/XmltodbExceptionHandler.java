package com.nju.software.xmltodb.advice;

import com.nju.software.common.utils.CodeEnum;
import com.nju.software.common.utils.Result;
import com.nju.software.xmltodb.exception.XmltodbException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.concurrent.CompletionException;

/**
 * @Description xmltodb模块自定义异常处理
 * @Author wxy
 * @Date 2024/2/19
 **/
@Slf4j
@RestControllerAdvice(basePackages = {"com.nju.software.xmltodb.controller"})
public class XmltodbExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result handleGlobalException(Exception e) {
        log.error("解析xml模块全局异常，错误码：{}，异常原因：{}，异常类型：{}, 堆栈：{}", CodeEnum.FAIL.getCode(), e.getMessage(), e.getClass(), e.getStackTrace());
        return Result.error();
    }

    @ExceptionHandler(value = XmltodbException.class)
    public Result handleXmltodbException(XmltodbException e) {
        log.error("解析xml模块异常，错误码：{}，异常原因：{}，异常类型：{}", e.getCode(), e.getMessage(), e.getClass());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = SizeLimitExceededException.class)
    public Result handleSizeLimitExceededException(Exception e) {
        log.error("解析xml模块异常，错误码：{}，异常原因：{}，异常类型：{}", CodeEnum.UPLOAD_FILE_SIZE_EXCEED.getCode(), e.getMessage(), e.getClass());
        return Result.error(CodeEnum.UPLOAD_FILE_SIZE_EXCEED.getCode(), CodeEnum.UPLOAD_FILE_SIZE_EXCEED.getMsg());
    }

    @ExceptionHandler(value = SQLException.class)
    public Result handleSQLException(Exception e) {
        log.error("解析xml模块异常，错误码：{}，异常原因：{}，异常类型：{}", CodeEnum.SQL_EXCEPTION.getCode(), e.getMessage(), e.getClass());
        return Result.error(CodeEnum.SQL_EXCEPTION.getCode(), CodeEnum.SQL_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = DuplicateKeyException.class)
    public Result handleDuplicateKeyException(Exception e) {
        log.error("解析xml模块异常，错误码：{}，异常原因：{}，异常类型：{}", CodeEnum.DUPLICATE_KEY_EXCEPTION.getCode(), e.getMessage(), e.getClass());
        return Result.error(CodeEnum.DUPLICATE_KEY_EXCEPTION.getCode(), CodeEnum.DUPLICATE_KEY_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = MessagingException.class)
    public Result handleMessagingException(MessagingException e) {
        log.error("解析xml模块异常，错误码：{}，异常原因：{}，异常类型：{}", CodeEnum.MESSAGE_EXCEPTION.getCode(), e.getMessage(), e.getClass());
        return Result.error(CodeEnum.MESSAGE_EXCEPTION.getCode(), CodeEnum.MESSAGE_EXCEPTION.getMsg());
    }

    @ExceptionHandler(value = CompletionException.class)
    public Result handleCompletionException(Exception completionException) {
        log.error("多线程运行出错:{}", completionException.getMessage());
        Throwable e = completionException.getCause();
        Result result = Result.error(CodeEnum.FAIL.getCode(), CodeEnum.FAIL.getMsg());
        if (e instanceof DuplicateKeyException) {
            result = handleDuplicateKeyException((Exception) e);
        } else if (e instanceof SQLException) {
            result = handleSQLException((Exception) e);
        }
        return result;
    }


}
