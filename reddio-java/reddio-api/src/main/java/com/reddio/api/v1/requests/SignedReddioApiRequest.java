package com.reddio.api.v1.requests;

import com.reddio.api.v1.rest.Signature;

/**
 * SignedReddioApiRequest is the base interface for all requests to the Reddio API that require a signature.
 *
 * @param <RequestType>  The type of the request.
 * @param <ResponseType> The type of the response.
 */
public interface SignedReddioApiRequest<RequestType, ResponseType> extends ReddioApiRequest<RequestType, ResponseType> {
    /**
     * Returns the signature of the request.
     *
     * @return The signature.
     */
    Signature getSignature();
}
