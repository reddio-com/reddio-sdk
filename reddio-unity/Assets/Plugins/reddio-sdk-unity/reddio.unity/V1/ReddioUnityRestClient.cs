using System;
using System.Collections;
using Cysharp.Threading.Tasks;
using Reddio.Api.V1.Rest;

namespace reddio.unity.V1
{
    /**
     * This class is a wrapper around the Reddio.Api.V1.Rest.RedditClient class,
     * for providing Unity Coroutine Styled API. 
     */
    public class ReddioUnityRestClient
    {
        private IReddioRestClient _restClient;

        public ReddioUnityRestClient(ReddioRestClient restClient)
        {
            _restClient = restClient;
        }

        public IEnumerator Transfer(
            TransferMessage transferMessage,
            Action<ResponseWrapper<TransferResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.Transfer(transferMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetNonce(
            GetNonceMessage getNonceMessage,
            Action<ResponseWrapper<GetNonceResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetNonce(getNonceMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetAssetId(
            GetAssetIdMessage getAssetIdMessage,
            Action<ResponseWrapper<GetAssetIdResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetAssetId(getAssetIdMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetVaultId(
            GetVaultIdMessage getVaultIdMessage,
            Action<ResponseWrapper<GetVaultIdResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetVaultId(getVaultIdMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetRecord(
            GetRecordMessage getRecordMessage,
            Action<ResponseWrapper<GetRecordResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetRecord(getRecordMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetRecords(
            GetRecordsMessage getRecordsMessage,
            Action<ResponseWrapper<GetRecordsResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetRecords(getRecordsMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetBalance(
            GetBalanceMessage getBalanceMessage,
            Action<ResponseWrapper<GetBalanceResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetBalance(getBalanceMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetBalances(
            GetBalancesMessage getBalancesMessage,
            Action<ResponseWrapper<GetBalancesResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetBalances(getBalancesMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator GetContractInfo(
            GetContractInfoMessage getContractInfoMessage,
            Action<ResponseWrapper<GetContractInfoResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetContractInfo(getContractInfoMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator OrderInfo(
            OrderInfoMessage orderInfoMessage,
            Action<ResponseWrapper<OrderInfoResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.OrderInfo(orderInfoMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator OrderList(
            OrderListMessage orderListMessage,
            Action<ResponseWrapper<OrderListResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.OrderList(orderListMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }

        public IEnumerator Collection(
            CollectionMessage collectionMessage,
            Action<ResponseWrapper<CollectionResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.Collection(collectionMessage).AsUniTask()
                .ToCoroutine(resultHandler, exceptionHandler);
        }
    }
}