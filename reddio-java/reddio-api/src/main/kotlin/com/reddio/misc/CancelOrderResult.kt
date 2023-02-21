package com.reddio.misc

import com.reddio.api.v1.rest.Order

data class CancelOrderResult(
    val succeed: Boolean,
    val order: Order?,
    val exception: Throwable?
)