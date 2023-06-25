package com.reddio.api.misc;

import com.reddio.api.v1.rest.ResponseWrapper;
import com.reddio.exception.ReddioBusinessException;
import com.reddio.exception.ReddioErrorCode;

public class EnsureSuccess {
    public static <T> ResponseWrapper<T> ensureSuccess(ResponseWrapper<T> responseWrapper, String... messages) {
        if ("OK".equals(responseWrapper.getStatus())) {
            return responseWrapper;
        }
        throw new ReddioBusinessException(responseWrapper.getStatus(), responseWrapper.getError(), ReddioErrorCode.fromCode(responseWrapper.getErrorCode()), responseWrapper);
    }
}
