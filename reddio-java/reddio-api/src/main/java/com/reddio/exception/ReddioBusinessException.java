package com.reddio.exception;

import com.reddio.api.v1.rest.ResponseWrapper;
import jnr.x86asm.RID;
import lombok.Getter;

public class ReddioBusinessException extends ReddioException {

    private ReddioErrorCode errorCode;
    @Getter
    private ResponseWrapper<Object> response;
}
