package com.conti_talent.springboot.appweb.conti_talent_web.exception;

/** Error de regla de negocio: validaciones, conflictos, datos duplicados, etc. */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
