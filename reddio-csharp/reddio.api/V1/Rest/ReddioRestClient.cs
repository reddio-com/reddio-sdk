using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

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
            client.DefaultRequestHeaders.UserAgent.Add(new ProductInfoHeaderValue("reddio-client-csharp", "0.0.1"));
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
            var endpoint =
                $"{_baseEndpoint}/v1/assetid?type={getAssetIdMessage.Type}&contract_address={getAssetIdMessage.ContractAddress}&token_id={getAssetIdMessage.TokenId}";
            var request = new HttpRequestMessage(HttpMethod.Get, endpoint);
            request.Content = new StringContent("", Encoding.UTF8, "application/json");
            var client = HttpClientWithReddioUA();
            var response = await client.SendAsync(request);
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
            var body = await response.Content.ReadAsStringAsync();
            response.EnsureSuccessStatusCode();
            var result = await ReadAsJsonAsync<ResponseWrapper<GetRecordResponse>>(response);
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