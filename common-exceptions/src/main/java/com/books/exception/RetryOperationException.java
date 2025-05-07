package com.books.exception;

public class RetryOperationException extends RuntimeException {
    public RetryOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}