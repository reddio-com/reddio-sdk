using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace reddio.api.V2.Rest
{
    public class ReddioRestClient : IReddioRestClient
    {
        private const string MainnetApiEndpoint = "https://api.reddio.com";
        private const string TestnetApiEndpoint = "https://api-dev.reddio.com";


        private string _baseEndpoint;

        public ReddioRestClient(string baseEndpoint)
        {
            _baseEndpoint = baseEndpoint;
        }

        private static HttpClient HttpClientWithReddioUa()
        {
            var client = new HttpClient();
            // TODO(@STRRL): use the release version
            client.DefaultRequestHeaders.UserAgent.Add(new ProductInfoHeaderValue("reddio-client-csharp",
                Versions.getUAVersion()));
            return client;
        }

        public async Task<ResponseWrapper<GetBalanceResponse>> GetBalance(GetBalanceMessage getBalanceMessage)
        {
            var query = System.Web.HttpUtility.ParseQueryString(string.Empty);
            query["stark_key"] = getBalanceMessage.StarkKey;
            if (getBalanceMessage.Limit != null)
            {
                query["limit"] = getBalanceMessage.Limit;
            }

            if (getBalanceMessage.Page != null)
            {
                query["page"] = getBalanceMessage.Page;
            }

            if (getBalanceMessage.ContractAddress != null)
            {
                query["contract_address"] = getBalanceMessage.ContractAddress;
            }

            var uri = new Uri($"{_baseEndpoint}/v2/balances?{query}");
            var client = HttpClientWithReddioUa();
            var response = await client.GetAsync(uri.ToString());
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetBalanceResponse>>(response);
            return result!;
        }

        private static async Task<T> ReadAsJsonAsync<T>(HttpResponseMessage response)
        {
            var responseContent = await response.Content.ReadAsStringAsync();
            return JsonConvert.DeserializeObject<T>(responseContent)!;
        }
        
        public static ReddioRestClient Mainnet()
        {
            return new ReddioRestClient(MainnetApiEndpoint);
        }

        public static ReddioRestClient Testnet()
        {
            return new ReddioRestClient(TestnetApiEndpoint);
        }
    }
}