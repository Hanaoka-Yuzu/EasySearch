package com.nju.software.xmltodb.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description xmltodb服务自定义异常
 * @Author wxy
 * @Date 2024/2/18
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class XmltodbException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 473960548860982926L;

    private String message;

    private Integer code;

    public XmltodbException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

}
