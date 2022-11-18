using System;
using System.Threading;
using System.Threading.Tasks;
using Reddio.Api.V1.Rest;

namespace Reddio.Api.V1
{
    /// <summary>
    /// High-Level API.
    /// </summary>
    public interface IReddioClient
    {
        public Task<ResponseWrapper<TransferResponse>> Transfer(
            string starkKey,
            string privateKey,
            string amount,
            string contractAddress,
            string tokenId,
            string type,
            string receiver,
            Int64 expirationTimeStamp
        );

        public Task<ResponseWrapper<GetRecordResponse>> GetRecord(string starkKey, Int64 sequenceId);

        public Task<ResponseWrapper<GetRecordsResponse>> GetRecords(
            string starkKey,
            long? limit = null,
            long? page = null,
            string? contractAddress = null
        );

        public Task<ResponseWrapper<GetRecordResponse>> WaitingTransferGetAccepted(string starkKey, Int64 sequenceId);

        public Task<ResponseWrapper<GetRecordResponse>> WaitingTransferGetAccepted(
            string starkKey,
            Int64 sequenceId,
            TimeSpan interval,
            CancellationToken cancellationToken
        );

        public Task<ResponseWrapper<GetBalanceResponse>> GetBalance(string starkKey, string assetId);
        public Task<ResponseWrapper<GetBalancesResponse>> GetBalances(string starkKey);

        [Obsolete]
        public Task<ResponseWrapper<OrderResponse>> Order(
            string privateKey,
            string starkKey,
            string price,
            string amount,
            string tokenAddress,
            string tokenId,
            string marketplaceUuid,
            string tokenType,
            OrderType orderType
        );

        public Task<ResponseWrapper<OrderResponse>> Order(
            string privateKey,
            string starkKey,
            string contractType,
            string contractAddress,
            string tokenId,
            string price,
            string amount,
            OrderType orderType,
            string baseTokenType = "ETH",
            string baseTokenContract = "eth",
            string marketplaceUuid = ""
        );
    }
}