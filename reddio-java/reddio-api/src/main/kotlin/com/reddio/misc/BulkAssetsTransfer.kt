package com.reddio.misc

import com.reddio.exception.ReddioException
import com.reddio.api.v1.StarkExSigner
import com.reddio.api.v1.requests.ReddioCancelOrderApi
import com.reddio.api.v1.requests.ReddioTransferToApi
import com.reddio.api.v1.rest.*
import com.reddio.api.v1.rest.GetBalancesResponse.BalanceRecord
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import java.util.stream.Collectors

/**
 * BulkAssetsTransfer is a helper class which would transfer **all the assets** from sender to receiver as best effort.
 *
 * <p>
 * It would:
 *
 * - list the balances of the sender, including ETH, ERC20 and ERC721
 * - cancel all placed orders
 * - transfer assets to the receiver as best effort
 * - polling on the status of the transfer, return the result when all the assets are resolved
 */
class BulkAssetsTransfer(
    private val restClient: DefaultReddioRestClient,
    private val senderStarkPrivateKey: String,
    private val receiverStarkKey: String
) {
    private val senderStarkExSigner = StarkExSigner(senderStarkPrivateKey)


    fun bulkTransfer(): BulkAssetsTransferResult {
        return runBlocking {
            val orderCancellationResults = listPlacedOrders().parallelStream().map { order ->
                ReddioCancelOrderApi.cancelOrder(
                    restClient, senderStarkPrivateKey, order.getOrderId()
                ).callAndPollOrderAsync().thenApply { CancelOrderResult(true, it, null) }
                    .exceptionally { CancelOrderResult(false, null, it) }
            }.map { it.join() }.collect(Collectors.toList())

            // TODO: collect the results of the order cancellation, mapping to balances.
            val transferEntries = listBalancesOfSender().parallelStream().filter {
                it.balanceAvailable > 0L
            }.map {
                try {
                    val transferRecord = transferAsset(it);
                    if (transferRecord.getStatus() == RecordStatus.FailedOnReddio) {
                        throw ReddioException("failed to transfer asset, sequence id: ${transferRecord.sequenceId}, status: ${transferRecord.status}, resp: ${transferRecord.resp}")
                    }
                    BulkAssetsTransferResultEntry(true, it, transferRecord, null)
                } catch (t: Throwable) {
                    BulkAssetsTransferResultEntry(false, it, null, t)
                }
            }.collect(Collectors.toList())

            BulkAssetsTransferResult(
                transferEntries.all { it.succeed },
                transferEntries.count { it.succeed }.toLong(),
                transferEntries.count { !it.succeed }.toLong(),
                transferEntries.size.toLong(),
                transferEntries
            )
        }
    }

    private fun transferAsset(balanceRecord: BalanceRecord): SequenceRecord {
        if (balanceRecord.balanceAvailable <= 0L) {
            // TODO: use certain type of exception
            throw ReddioException("insufficient balance to transfer")
        }

        if (balanceRecord.balanceFrozen > 0L) {
            // TODO: use certain type of exception
            throw ReddioException("balance is frozen, cannot transfer, type: ${balanceRecord.type}, contract address: ${balanceRecord.contractAddress}, token id: ${balanceRecord.tokenId}, balance frozen: ${balanceRecord.balanceFrozen}")
        }

        return ReddioTransferToApi.transfer(
            this.restClient,
            this.senderStarkPrivateKey,
            balanceRecord.displayValue,
            balanceRecord.contractAddress,
            balanceRecord.tokenId,
            balanceRecord.type,
            this.receiverStarkKey,
            4194303L,
        ).callAndPollRecord()
    }

    private suspend fun listPlacedOrders(): List<Order> {
        val result = mutableListOf<Order>()
        var done = false;
        var page = 1L;

        while (!done) {
            val response = restClient.orderList(
                OrderListMessage.of(
                    senderStarkExSigner.getStarkKey(), null, 100L, page, null, null, OrderState.Placed
                )
            ).await()

            if (response.getData().total == 0L) {
                return emptyList()
            }
            result.addAll(response.getData().list)
            if (response.getData().currentPage == response.getData().totalPage) {
                done = true
            } else {
                page += 1
            }
        }


        return result
    }

    private suspend fun listBalancesOfSender(): List<GetBalancesResponse.BalanceRecord> {
        val result = mutableListOf<GetBalancesResponse.BalanceRecord>()
        var done = false
        var page = 1L
        while (!done) {
            val response = restClient.getBalances(
                GetBalancesMessage.of(senderStarkExSigner.getStarkKey(), null, 100L, page)
            ).await()
            if (response.getData().total == 0L) {
                return emptyList()
            }
            result.addAll(response.getData().list)
            if (response.getData().currentPage == response.getData().totalPage) {
                done = true
            } else {
                page += 1
            }
        }
        return result
    }
}