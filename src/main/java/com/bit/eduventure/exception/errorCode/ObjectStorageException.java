package com.bit.eduventure.exception.errorCode;

public class ObjectStorageException extends RuntimeException {
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}