package com.reddio.api.v1.requests

import com.reddio.api.v1.DefaultReddioClient
import com.reddio.api.v1.rest.GetAssetIdMessage
import com.reddio.api.v1.rest.GetContractInfoMessage
import com.reddio.api.v1.rest.GetVaultIdMessage
import com.reddio.api.v1.rest.ReddioRestClient
import kotlinx.coroutines.future.await

/**
 * The helper class to get the metadata of the asset, like asset id, asset type, vault ids, etc.
 */
class AssetVaultHelper {

    companion object {
        /**
         * get asset id for the given asset on layer 2.
         *
         * @param restClient the reddio rest client
         * @param contractAddress the contract address of the asset, use literal "ETH" for ETH
         * @param tokenId the token id of the ERC721, use empty string for ETH and ERC20
         * @param type the type of the asset, use literal "ERC20" for ERC20, "ERC721" for ERC721, "ETH" for ETH
         */
        suspend fun getAssetId(
            restClient: ReddioRestClient,
            contractAddress: String,
            tokenId: String,
            type: String
        ): String {
            val contractInfo =
                restClient.getContractInfo(GetContractInfoMessage.of(type, contractAddress)).await().getData()
            val result =
                restClient.getAssetId(GetAssetIdMessage.of(contractAddress, type, tokenId, contractInfo.quantum))
                    .await()
            return result.getData().getAssetId()
        }

        /**
         * get vault ids for the given asset and stark keys.
         *
         * @param restClient the reddio rest client
         * @param assetId the asset id of the asset
         * @param sender the stark key of the sender
         * @param receiver the stark key or eth address of the receiver
         */
        suspend fun getVaultsIds(
            restClient: ReddioRestClient,
            assetId: String,
            sender: String,
            receiver: String
        ): VaultIds {
            val result = restClient.getVaultId(GetVaultIdMessage.of(assetId, listOf(sender, receiver))).await()
            return VaultIds(result.getData().vaultIds[0], result.getData().vaultIds[1])
        }

        data class VaultIds(val senderVaultId: String, val receiverVaultId: String)
    }
}