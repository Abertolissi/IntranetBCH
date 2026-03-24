package com.banco.intranet.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * Excepción personalizada para la aplicación
 */
@Getter
@Setter
public class AppException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private int httpStatus;

    public AppException(String message) {
        super(message);
        this.errorCode = "APP_ERROR";
        this.httpStatus = 500;
    }

    public AppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = 500;
    }

    public AppException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public AppException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = 500;
    }

    public AppException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
