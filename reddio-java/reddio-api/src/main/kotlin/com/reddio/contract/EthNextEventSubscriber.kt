package com.reddio.contract

import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import kotlinx.coroutines.future.await
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterNumber
import java.math.BigInteger
import java.util.concurrent.CompletableFuture

/**
 * EthNextEventSubscriber would subscribe the next event from currentBlockNumber -10 to +5.
 */
class EthNextEventSubscriber<T>(
    private val eventFlowSupplier: BiFunction<DefaultBlockParameter, DefaultBlockParameter, Flowable<T>>,
    private val web3j: Web3j,
) {

    suspend fun subscribeNextEvent(): T {
        val currentBlock = web3j.ethBlockNumber().send()
        val from = DefaultBlockParameterNumber(currentBlock.blockNumber.subtract(BigInteger("10")))
        val to = DefaultBlockParameterNumber(currentBlock.blockNumber.add(BigInteger("5")))
        return subscribeNextEvent(eventFlowSupplier.apply(from, to))
    }

    private suspend fun <T> subscribeNextEvent(flowable: Flowable<T>): T {
        val future = CompletableFuture<T>()
        val subscription = flowable.subscribe({ future.complete(it) }, { future.completeExceptionally(it) })
        return try {
            future.await()
        } finally {
            subscription.dispose()
        }
    }

    companion object {
        fun <T> create(
            eventFlowSupplier: BiFunction<DefaultBlockParameter, DefaultBlockParameter, Flowable<T>>,
            web3j: Web3j,
        ): EthNextEventSubscriber<T> {
            return EthNextEventSubscriber(eventFlowSupplier, web3j)
        }
    }
}