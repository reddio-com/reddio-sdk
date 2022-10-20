package com.reddio.api.v1

import com.reddio.api.v1.rest.GetRecordResponse

class TransferFailedException(override val message: String?, val record: GetRecordResponse) : RuntimeException() {
}