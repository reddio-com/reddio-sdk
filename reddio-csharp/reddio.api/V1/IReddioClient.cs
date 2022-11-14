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

        public Task<ResponseWrapper<GetRecordsResponse>> GetRecords(string starkKey);

        public Task<ResponseWrapper<GetRecordResponse>> WaitingTransferGetAccepted(string starkKey, Int64 sequenceId);

        public Task<ResponseWrapper<GetRecordResponse>> WaitingTransferGetAccepted(
            string starkKey,
            Int64 sequenceId,
            TimeSpan interval,
            CancellationToken cancellationToken
        );

        public Task<ResponseWrapper<GetBalanceResponse>> GetBalance(string starkKey, string assetId);
        public Task<ResponseWrapper<GetBalancesResponse>> GetBalances(string starkKey);
    }
}