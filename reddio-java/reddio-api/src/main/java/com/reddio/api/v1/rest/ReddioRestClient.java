package com.reddio.api.v1.rest;

import java.util.concurrent.CompletableFuture;

public interface ReddioRestClient {
    /**
     * Transfer assets from sender to receiver on layer 2.
     * <p>
     * See API References: <a href="https://docs.reddio.com/api/layer2-apis.html#transfer">https://docs.reddio.com/api/layer2-apis.html#transfer</a>
     *
     * @param transferMessage
     * @return
     */
    CompletableFuture<ResponseWrapper<TransferResponse>> transfer(TransferMessage transferMessage);

    /**
     * Retrieve the unique nonce by stark_key
     * <p>
     * See API References: <a href="https://docs.reddio.com/api/layer2-apis.html#get-nonce-by-stark-key">https://docs.reddio.com/api/layer2-apis.html#get-nonce-by-stark-key</a>
     *
     * @param getNonceMessage
     * @return
     */
    CompletableFuture<ResponseWrapper<GetNonceResponse>> getNonce(GetNonceMessage getNonceMessage);

    /**
     * Retrieve asset id based on contract address
     * <p>
     * See API References: <a href="https://docs.reddio.com/api/layer2-apis.html#get-asset-id">https://docs.reddio.com/api/layer2-apis.html#get-asset-id</a>
     *
     * @param getAssetIdMessage
     * @return
     */
    CompletableFuture<ResponseWrapper<GetAssetIdResponse>> getAssetId(GetAssetIdMessage getAssetIdMessage);

    /**
     * Retrieve the vault id
     * <p>
     * See API References: <a href="https://docs.reddio.com/api/layer2-apis.html#retrieve-the-vault">https://docs.reddio.com/api/layer2-apis.html#retrieve-the-vault</a>
     *
     * @param getVaultIdMessage
     * @return
     */
    CompletableFuture<ResponseWrapper<GetVaultIdResponse>> getVaultId(GetVaultIdMessage getVaultIdMessage);

    /**
     * Retrieve record based on start_key and sequence id
     * <p>
     * See API References: <a href="https://docs.reddio.com/api/layer2-apis.html#get-record">https://docs.reddio.com/api/layer2-apis.html#get-record</a>
     *
     * @param getRecordMessage
     * @return
     */
    CompletableFuture<ResponseWrapper<GetRecordResponse>> getRecord(GetRecordMessage getRecordMessage);
}
