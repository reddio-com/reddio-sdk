package com.reddio.exception;

import com.reddio.api.v1.rest.ResponseWrapper;
import jnr.x86asm.RID;
import lombok.Getter;


/**
 * ReddioBusinessException represents the business exception that return by the reddio service.
 */
public class ReddioBusinessException extends ReddioException {
    private static final long serialVersionUID = -6477748788283734748L;

    /**
     * The status in the response.
     */
    @Getter
    private final String status;

    /**
     * Error message.
     */
    @Getter
    private final String error;

    /**
     * Error code as enum, might be null if the error code is not recognized.
     * <p/>
     * You could still get the error code from raw response {@link #getResponse()}.
     */
    @Getter
    private final ReddioErrorCode errorCode;

    /**
     * Raw response from the reddio service.
     */
    @Getter
    private final ResponseWrapper<?> response;

    public ReddioBusinessException(String status, String error, ReddioErrorCode errorCode, ResponseWrapper<?> response) {
        this.status = status;
        this.error = error;
        this.errorCode = errorCode;
        this.response = response;
    }

    @Override
    public String getMessage() {
        return String.format("reddio business failure, status: %s, error: %s, error code: %s", status, error, errorCode);
    }
}
