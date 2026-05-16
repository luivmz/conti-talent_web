package com.conti_talent.springboot.appweb.conti_talent_web.exception;

/** Lanzada cuando se intenta una transición de estado no permitida. */
public class EstadoInvalidoException extends BusinessException {
    public EstadoInvalidoException(String message) {
        super(message);
    }
}
