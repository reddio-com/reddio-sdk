package com.reddio.api.v1.requests

import java.util.concurrent.CompletionException

fun unwrapCompletionException(e: Throwable): Throwable {
    return if (e is CompletionException) {
        e.cause ?: e
    } else {
        e
    }
}

fun <T> unwrapCompletionException(f: () -> T): T {
    try {
        return f()
    } catch (e: CompletionException) {
        throw unwrapCompletionException(e)
    } catch (t: Throwable) {
        throw t
    }
}