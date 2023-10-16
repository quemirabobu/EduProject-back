package com.bit.eduventure.exception.errorCode;

public class MakeSignatureException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
