package com.reddio.api.v1

import com.reddio.exception.ReddioException
import com.reddio.api.v1.rest.GetRecordResponse

class TransferFailedException(override val message: String?, val record: GetRecordResponse) : ReddioException(message) {
    override fun toString(): String {
        return "TransferFailedException(message=$message, record=$record)"
    }
}