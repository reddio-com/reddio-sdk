using System.Threading.Tasks;

namespace Reddio.Api.V1.Rest
{
    /// <summary>
    /// Low-Level API.
    /// </summary>
    public interface IReddioRestClient
    {
        /// <summary>
        /// Transfer assets from sender to receiver on layer 2.
        /// 
        /// See API References: https://docs.reddio.com/api/layer2-apis.html#transfer
        /// </summary>
        /// <param name="transferMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<TransferResponse>> Transfer(TransferMessage transferMessage);

        /// <summary>
        /// Retrieve the unique nonce by stark_key
        /// 
        /// See API References: https://docs.reddio.com/api/layer2-apis.html#get-nonce-by-stark-key
        /// </summary>
        /// <param name="getNonceMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<GetNonceResponse>> GetNonce(GetNonceMessage getNonceMessage);


        /// <summary>
        /// Retrieve asset id based on contract address
        ///
        /// See API References: https://docs.reddio.com/api/layer2-apis.html#get-asset-id
        /// </summary>
        /// <param name="getAssetIdMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<GetAssetIdResponse>> GetAssetId(GetAssetIdMessage getAssetIdMessage);

        /// <summary>
        /// Retrieve the vault id
        ///
        /// See API References: https://docs.reddio.com/api/layer2-apis.html#retrieve-the-vault
        /// </summary>
        /// <param name="getVaultIdMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<GetVaultIdResponse>> GetVaultId(GetVaultIdMessage getVaultIdMessage);

        /// <summary>
        /// Retrieve record based on start_key and sequence id
        ///
        /// See API References: https://docs.reddio.com/api/layer2-apis.html#get-record
        /// </summary>
        /// <param name="getRecordMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<GetRecordResponse>> GetRecord(GetRecordMessage getRecordMessage);

        public Task<ResponseWrapper<GetRecordsResponse>> GetRecords(GetRecordsMessage getRecordsMessage);

        /// <summary>
        /// Retrieve account balance based on the stark_key and asset_id.
        ///
        /// See API Reference: https://docs.reddio.com/api/layer2-apis.html#get-balance
        /// </summary>
        /// <param name="getBalanceMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<GetBalanceResponse>> GetBalance(GetBalanceMessage getBalanceMessage);

        /// <summary>
        /// Retrieve account balances in batch based on the stark_key
        ///
        /// See API Reference: https://docs.reddio.com/api/layer2-apis.html#get-balances
        /// </summary>
        /// <param name="getBalancesMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<GetBalancesResponse>> GetBalances(GetBalancesMessage getBalancesMessage);
    }
}