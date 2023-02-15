package com.reddio.api.v1.requests;

import java.util.concurrent.CompletableFuture;

/**
 * ReddioApiRequest is the base interface for all requests to the Reddio API.
 *
 * @param <RequestType>  The type of the request.
 * @param <ResponseType> The type of the response.
 */
public interface ReddioApiRequest<RequestType, ResponseType> {

    /**
     * Sends the request synchronously.
     *
     * @return The response.
     */
    ResponseType call();

    /**
     * Sends the request asynchronously.
     *
     * @return A CompletableFuture that will be completed with the response.
     */
    CompletableFuture<ResponseType> callAsync();

    /**
     * Returns the raw request.
     *
     * @return The raw request.
     */
    RequestType getRequest();
}
