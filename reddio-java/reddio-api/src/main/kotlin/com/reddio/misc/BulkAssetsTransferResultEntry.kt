package com.reddio.misc

import com.reddio.api.v1.rest.GetBalancesResponse
import com.reddio.api.v1.rest.SequenceRecord

data class BulkAssetsTransferResultEntry(
    val succeed: Boolean,
    val transferredAsset: GetBalancesResponse.BalanceRecord,
    val transferRecord: SequenceRecord?,
    val exception: Throwable?
)