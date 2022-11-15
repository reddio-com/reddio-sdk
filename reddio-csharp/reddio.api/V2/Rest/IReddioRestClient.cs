using System.Threading.Tasks;

namespace reddio.api.V2.Rest
{
    public interface IReddioRestClient
    {
        /// <summary>
        /// Retrieve account balance based on the stark_key and asset_id.
        ///
        /// See API Reference: https://docs.reddio.com/api/layer2-apis.html#get-balance
        /// </summary>
        /// <param name="getBalanceMessage"></param>
        /// <returns></returns>
        public Task<ResponseWrapper<GetBalanceResponse>> GetBalance(GetBalanceMessage getBalanceMessage);
    }
}