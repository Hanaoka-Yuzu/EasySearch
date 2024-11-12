package com.nju.software.user.exception;

import com.nju.software.common.utils.CodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class UserException extends RuntimeException {

    private final Integer code; //状态码

    public UserException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public UserException(Integer code) {
        super();
        this.code = code;
    }

    public UserException(Throwable cause, Integer code) {
        super(cause);
        this.code = code;
    }

    public UserException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }

    public UserException(CodeEnum codeEnum) {
        super(codeEnum.getMsg());
        this.code = codeEnum.getCode();
    }
}