using System;
using System.Collections;
using System.Threading;
using Cysharp.Threading.Tasks;
using Reddio.Api.V1;
using Reddio.Api.V1.Rest;

namespace reddio.unity.V1
{
    public class ReddioUnityClient
    {
        private IReddioClient _reddioClient;

        public ReddioUnityClient(IReddioClient reddioClient)
        {
            _reddioClient = reddioClient;
        }

        public IEnumerator Transfer(
            string starkKey,
            string privateKey,
            string amount,
            string contractAddress,
            string tokenId,
            string type,
            string receiver,
            long expirationTimeStamp,
            Action<ResponseWrapper<TransferResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _reddioClient.Transfer(
                starkKey,
                privateKey,
                amount,
                contractAddress,
                tokenId,
                type,
                receiver,
                expirationTimeStamp
            ).AsUniTask().ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetRecord(
            string starkKey,
            long sequenceId,
            Action<ResponseWrapper<GetRecordResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _reddioClient.GetRecord(
                starkKey,
                sequenceId
            ).AsUniTask().ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetRecords(
            string starkKey,
            long? limit,
            long? page,
            string contractAddress,
            Action<ResponseWrapper<GetRecordsResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _reddioClient.GetRecords(
                starkKey,
                limit,
                page,
                contractAddress).AsUniTask().ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator WaitingTransferGetAccepted(
            string starkKey, long sequenceId,
            Action<ResponseWrapper<GetRecordResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _reddioClient.WaitingTransferGetAccepted(starkKey, sequenceId).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator WaitingTransferGetAccepted(
            string starkKey,
            Int64 sequenceId,
            TimeSpan interval,
            CancellationToken cancellationToken,
            Action<ResponseWrapper<GetRecordResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _reddioClient.WaitingTransferGetAccepted(starkKey, sequenceId, interval, cancellationToken)
                .AsUniTask().ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetBalance(
            string starkKey, string assetId,
            Action<ResponseWrapper<GetBalanceResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _reddioClient.GetBalance(starkKey, assetId).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetBalances(
            string starkKey,
            Action<ResponseWrapper<GetBalancesResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _reddioClient.GetBalances(starkKey).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator Order(
            string privateKey,
            string starkKey,
            string contractType,
            string contractAddress,
            string tokenId,
            string price,
            string amount,
            OrderType orderType,
            string baseTokenType,
            string baseTokenContract,
            string marketplaceUuid,
            Action<ResponseWrapper<OrderResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _reddioClient.Order(
                privateKey,
                starkKey,
                contractType,
                contractAddress,
                tokenId,
                price,
                amount,
                orderType,
                baseTokenType,
                baseTokenContract,
                marketplaceUuid
            ).AsUniTask().ToCoroutine(resultHandler, exceptionHandler);
        }
    }
}