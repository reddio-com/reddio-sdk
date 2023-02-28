package com.reddio.exception;

public class ReddioException extends RuntimeException {

    public ReddioException() {
        super();
    }

    public ReddioException(String message) {
        super(message);
    }

    public ReddioException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReddioException(Throwable cause) {
        super(cause);
    }
}
