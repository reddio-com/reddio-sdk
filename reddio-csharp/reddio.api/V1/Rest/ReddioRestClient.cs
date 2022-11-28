using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using Newtonsoft.Json;
using reddio.api;

namespace Reddio.Api.V1.Rest
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

        private static HttpClient HttpClientWithReddioUA()
        {
            var client = new HttpClient();
            // TODO(@STRRL): use the release version
            client.DefaultRequestHeaders.UserAgent.Add(new ProductInfoHeaderValue("reddio-client-csharp",
                Versions.getUAVersion()));
            return client;
        }

        private static HttpContent JsonStringContent<T>(T payload)
        {
            var jsonString = JsonConvert.SerializeObject(payload);
            var content = new StringContent(jsonString, Encoding.UTF8, "application/json");
            return content;
        }

        private static async Task<T> ReadAsJsonAsync<T>(HttpResponseMessage response)
        {
            var responseContent = await response.Content.ReadAsStringAsync();
            return JsonConvert.DeserializeObject<T>(responseContent)!;
        }

        public async Task<ResponseWrapper<TransferResponse>> Transfer(TransferMessage transferMessage)
        {
            var endpoint = $"{_baseEndpoint}/v1/transfers";
            var client = HttpClientWithReddioUA();
            var response = await client.PostAsync(endpoint, JsonStringContent(transferMessage));
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<TransferResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<GetNonceResponse>> GetNonce(GetNonceMessage getNonceMessage)
        {
            var endpoint = $"{_baseEndpoint}/v1/nonce?stark_key={getNonceMessage.StarkKey}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetNonceResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<GetAssetIdResponse>> GetAssetId(GetAssetIdMessage getAssetIdMessage)
        {
            var query = HttpUtility.ParseQueryString(String.Empty);
            query["type"] = getAssetIdMessage.Type;
            query["contract_address"] = getAssetIdMessage.ContractAddress;
            query["token_id"] = getAssetIdMessage.TokenId;
            query["quantum"] = getAssetIdMessage.Quantum.ToString();

            var endpoint =
                $"{_baseEndpoint}/v1/assetid?{query}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            var result = await ReadAsJsonAsync<ResponseWrapper<GetAssetIdResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<GetVaultIdResponse>> GetVaultId(GetVaultIdMessage getVaultIdMessage)
        {
            var endpoint =
                $"{_baseEndpoint}/v1/vaults?asset_id={getVaultIdMessage.AssetId}&stark_keys={String.Join(",", getVaultIdMessage.StarkKeys)}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetVaultIdResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<GetRecordResponse>> GetRecord(GetRecordMessage getRecordMessage)
        {
            var endpoint =
                $"{_baseEndpoint}/v1/record?stark_key={getRecordMessage.StarkKey}&sequence_id={getRecordMessage.SequenceId}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetRecordResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<GetRecordsResponse>> GetRecords(GetRecordsMessage getRecordsMessage)
        {
            var query = HttpUtility.ParseQueryString(String.Empty);
            query["stark_key"] = getRecordsMessage.StarkKey;
            if (getRecordsMessage.Limit != null)
            {
                query["limit"] = getRecordsMessage.Limit;
            }

            if (getRecordsMessage.Page != null)
            {
                query["page"] = getRecordsMessage.Page;
            }

            if (getRecordsMessage.ContractAddress != null)
            {
                query["contract_address"] = getRecordsMessage.ContractAddress;
            }

            var endpoint =
                $"{_baseEndpoint}/v1/records?{query}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetRecordsResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<GetBalanceResponse>> GetBalance(GetBalanceMessage getBalanceMessage)
        {
            var endpoint =
                $"{_baseEndpoint}/v1/balance?stark_key={getBalanceMessage.StarkKey}&asset_id={getBalanceMessage.AssetId}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetBalanceResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<GetContractInfoResponse>> GetContractInfo(
            GetContractInfoMessage getContractInfoMessage)
        {
            var query = HttpUtility.ParseQueryString(String.Empty);
            query["type"] = getContractInfoMessage.Type;
            query["contract_address"] = getContractInfoMessage.ContractAddress;
            var endpoint =
                $"{_baseEndpoint}/v1/contract_info?{query}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetContractInfoResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<OrderInfoResponse>> OrderInfo(OrderInfoMessage orderInfoMessage)
        {
            var endpoint =
                $"{_baseEndpoint}/v1/order/info?stark_key={orderInfoMessage.StarkKey}&contract1={orderInfoMessage.Contract1}&contract2={orderInfoMessage.Contract2}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<OrderInfoResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<OrderResponse>> Order(OrderMessage orderMessage)
        {
            var endpoint = $"{_baseEndpoint}/v1/order";
            var client = HttpClientWithReddioUA();
            var response = await client.PostAsync(endpoint, JsonStringContent(orderMessage));
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<OrderResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<GetBalancesResponse>> GetBalances(GetBalancesMessage getBalancesMessage)
        {
            var query = HttpUtility.ParseQueryString(String.Empty);
            query["stark_key"] = getBalancesMessage.StarkKey;
            if (!String.IsNullOrEmpty(getBalancesMessage.ContractAddress))
            {
                query["contract_address"] = getBalancesMessage.ContractAddress;
            }

            var endpoint =
                $"{_baseEndpoint}/v1/balances?{query}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetBalancesResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<OrderListResponse>> OrderList(OrderListMessage orderListMessage)
        {
            var query = HttpUtility.ParseQueryString(String.Empty);
            query["stark_key"] = orderListMessage.StarkKey;
            query["contract_address"] = orderListMessage.ContractAddress;
            if (orderListMessage.TokenIds != null && orderListMessage.TokenIds.Length > 0)
            {
                query["token_ids"] = String.Join(",", orderListMessage.TokenIds);
            }

            var endpoint =
                $"{_baseEndpoint}/v1/orders?{query}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<OrderListResponse>>(response);
            return result!;
        }

        public async Task<ResponseWrapper<CollectionResponse>> Collection(CollectionMessage collectionMessage)
        {
            var query = HttpUtility.ParseQueryString(String.Empty);
            query["stark_key"] = collectionMessage.StarkKey;
            query["contract_address"] = collectionMessage.ContractAddress;
            if (collectionMessage.TokenIds != null && collectionMessage.TokenIds.Length > 0)
            {
                query["token_ids"] = String.Join(",", collectionMessage.TokenIds);
            }

            var endpoint =
                $"{_baseEndpoint}/v1/orders?{query}";
            var client = HttpClientWithReddioUA();
            var response = await client.GetAsync(endpoint);
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<CollectionResponse>>(response);
            return result!;
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