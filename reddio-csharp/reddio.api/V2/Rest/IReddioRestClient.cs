using System.Threading.Tasks;

namespace reddio.api.V2.Rest
{
    public interface IReddioRestClient
    {
        /// <summary>
        /// Retrieve account balances in batch based on the stark_key, this API aggregates tokens by contract_address.
        ///
        /// See API Reference: https://docs.reddio.com/guide/api-reference/balance.html#get-balances-v2
        /// </summary>
        /// <param name="getBalanceMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<GetBalanceResponse>> GetBalance(GetBalanceMessage getBalanceMessage);
    }
}