using System;
using System.Collections;
using Cysharp.Threading.Tasks;
using reddio.api.V2.Rest;

namespace reddio.unity.V2
{
    public class ReddioUnityRestClient
    {
        private IReddioRestClient _restClient;

        public ReddioUnityRestClient(IReddioRestClient restClient)
        {
            _restClient = restClient;
        }

        public IEnumerator GetBalance(
            GetBalanceMessage getBalanceMessage,
            Action<ResponseWrapper<GetBalanceResponse>> resultHandler,
            Action<Exception> exceptionHandler
        )
        {
            return _restClient.GetBalance(getBalanceMessage).AsUniTask().ToCoroutine(resultHandler, exceptionHandler);
        }
    }
}