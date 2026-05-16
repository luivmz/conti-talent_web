package com.conti_talent.springboot.appweb.conti_talent_web.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
