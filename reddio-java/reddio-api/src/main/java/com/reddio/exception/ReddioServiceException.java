package com.reddio.exception;

import lombok.Getter;

/**
 * ReddioServiceException represents the exception when the reddio service not respond successfully.
 *
 * @author strrl
 */
public class ReddioServiceException extends ReddioException {
    public ReddioServiceException(String description, int httpStatusCode, ReddioErrorCode errorCode, String responseBody) {
        this.description = description;
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.responseBody = responseBody;
    }

    private static final long serialVersionUID = 1205765559865993637L;

    /**
     * Description of the exception.
     */
    @Getter
    private final String description;

    /**
     * Http status code.
     */
    @Getter
    private final int httpStatusCode;

    /**
     * Error code as enum, might be null if the error code is not recognized.
     */
    @Getter
    private final ReddioErrorCode errorCode;

    /**
     * The raw response body as string.
     */
    @Getter
    private final String responseBody;


    @Override
    public String getMessage() {
        return String.format("reddio service not respond service, description: %s, http status code: %d, error code: %s, response body: %s", description, httpStatusCode, errorCode, responseBody);
    }
}
