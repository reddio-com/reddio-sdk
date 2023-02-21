package com.reddio.misc

data class BulkAssetsTransferResult(
    val allSucceed: Boolean,
    val succeedCount: Long,
    val failedCount: Long,
    val total: Long,
    val details: List<BulkAssetsTransferResultEntry>,
)

