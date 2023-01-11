package com.reddio.api.v1

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.Response
import org.web3j.protocol.core.methods.response.BaseEventResponse
import org.web3j.protocol.core.methods.response.EthBlock
import org.web3j.protocol.core.methods.response.EthBlockNumber
import org.web3j.tuples.generated.Tuple2
import java.math.BigInteger
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class BlockConfirmationRequiredEvents<T>(
    private val flowableProvider: (start: DefaultBlockParameter, end: DefaultBlockParameter) -> Flowable<T>,
    private val requiredBlockConfirmation: Long,
    private val web3j: Web3j
) where T : BaseEventResponse {
    suspend fun eventFlowable(startBlockNumber: BigInteger): Flowable<T> {
        var prevBlockNumber = startBlockNumber
        return blockNumberFlowable().flatMap<T> { ethBlockNumber ->
            val end = ethBlockNumber.blockNumber.subtract(BigInteger.valueOf(requiredBlockConfirmation))
            if (prevBlockNumber.toLong() > end.toLong()) {
                Flowable.empty<T>()
            }
            val result = Flowable.create(
                { subscriber ->
                    val disposable = flowableProvider(
                        DefaultBlockParameter.valueOf(prevBlockNumber), DefaultBlockParameter.valueOf(end)
                    ).subscribe({
                        subscriber.onNext(it)
                    }, {
                        subscriber.onError(it)
                    })

                    // the origin web3j would never actively complete the log flowable , so we need to complete it manually
                    // ref: https://github.com/web3j/web3j/discussions/1845
                    var cancelSchedule: ScheduledFuture<*>? = null
                    cancelSchedule = scheduledExecutorService.scheduleAtFixedRate({
                        val currentBlockNumber = web3j.ethBlockNumber().send().blockNumber
                        if (currentBlockNumber.toLong() - ethBlockNumber.blockNumber.toLong() > requiredBlockConfirmation) {
                            disposable.dispose()
                            subscriber.onComplete()
                            cancelSchedule?.cancel(false)
                        }
                        // TODO: do not hardcode blocktime
                    }, 0, BLOCK_TIME, TimeUnit.SECONDS)

                }, BackpressureStrategy.BUFFER
            )
            prevBlockNumber = end
            result
        }
    }

    suspend fun eventFlowableWithEthBlock(startBlockNumber: BigInteger): Flowable<Tuple2<T, EthBlock>> {
        return this.eventFlowable(startBlockNumber).flatMap { response ->
            val blockNumber = response.log.blockNumber!!
            web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), false)
                .flowable().map { Tuple2(response, it) }
        }
    }

    suspend fun blockNumberFlowable(): Flowable<EthBlockNumber> {
        return Flowable.create(
            { subscriber ->
                var prevBlockNumber = 0L
                val schedule = scheduledExecutorService.scheduleAtFixedRate(
                    {
                        val blockNumber = web3j.ethBlockNumber().send()
                        if (blockNumber.blockNumber.toLong() != prevBlockNumber) {
                            subscriber.onNext(blockNumber)
                            prevBlockNumber = blockNumber.blockNumber.toLong()
                        }
                        // eth would generate new block about every 12 seconds
                        // TODO: do not hardcode blocktime
                    }, 0, BLOCK_TIME, TimeUnit.SECONDS
                )
                subscriber.setCancellable {
                    schedule.cancel(false)
                }
            }, BackpressureStrategy.BUFFER
        )
    }

    companion object {
        // TODO: do not hardcode the block time
        const val BLOCK_TIME = 12L
        const val DEFAULT_BLOCK_CONFIRMATION = 16L

        // TODO: thread name formatter
        private val scheduledExecutorService = Executors.newScheduledThreadPool(1)
    }
}
