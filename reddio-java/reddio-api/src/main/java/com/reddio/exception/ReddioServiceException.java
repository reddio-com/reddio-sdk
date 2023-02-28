package com.reddio.exception;

import lombok.Getter;

/**
 * ReddioServiceException represents the exception when the reddio service is not used properly.
 *
 * @author strrl
 */
public class ReddioServiceException extends ReddioException {
    public ReddioServiceException(int httpStatusCode, String responseBody, String description) {
        this.httpStatusCode = httpStatusCode;
        this.responseBody = responseBody;
        this.description = description;
    }

    private static final long serialVersionUID = 1205765559865993637L;

    @Getter
    private int httpStatusCode;

    @Getter
    private String responseBody;

    @Getter
    private String description;

    @Override
    public String getMessage() {
        return String.format("reddio service not respond service, description: %s, http status code: %d, response body: %s", description, httpStatusCode, responseBody);
    }
}
