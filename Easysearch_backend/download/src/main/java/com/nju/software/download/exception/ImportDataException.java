package com.nju.software.download.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImportDataException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 473960548860982926L;

    private String message;

    private Integer code;

    public ImportDataException(String message, Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

}
