using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text;
using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class ReddioRestClient : IReddioRestClient
{
    private const String MainnetApiEndpoint = "https://api.reddio.com";
    private const String TestnetApiEndpoint = "https://api-dev.reddio.com";

    private String _baseEndpoint;

    public ReddioRestClient(string baseEndpoint)
    {
        _baseEndpoint = baseEndpoint;
    }

    private static HttpClient HttpClientWithReddioUA()
    {
        var client = new HttpClient();
        client.DefaultRequestHeaders.UserAgent.Add(new ProductInfoHeaderValue("reddio", "0.0.1"));
        return client;
    }

    public async Task<ResponseWrapper<TransferResponse>> Transfer(TransferMessage transferMessage)
    {
        var endpoint = $"{_baseEndpoint}/v1/transfer";
        var client = HttpClientWithReddioUA();
        var response = await client.PostAsJsonAsync(endpoint, transferMessage);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ResponseWrapper<TransferResponse>>();
        return result!;
    }

    public async Task<ResponseWrapper<GetNonceResponse>> GetNonce(GetNonceMessage getNonceMessage)
    {
        var endpoint = $"{_baseEndpoint}/v1/nonce?stark_key={getNonceMessage.StarkKey}";
        var client = HttpClientWithReddioUA();
        var response = await client.GetAsync(endpoint);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ResponseWrapper<GetNonceResponse>>();
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
        var result = await response.Content.ReadFromJsonAsync<ResponseWrapper<GetAssetIdResponse>>();
        return result!;
    }

    public async Task<ResponseWrapper<GetVaultIdResponse>> GetVaultId(GetVaultIdMessage getVaultIdMessage)
    {
        var endpoint =
            $"{_baseEndpoint}/v1/vaults?asset_id={getVaultIdMessage.AssetId}&stark_keys={String.Join(",", getVaultIdMessage.StarkKeys)}";
        var client = HttpClientWithReddioUA();
        var response = await client.GetAsync(endpoint);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ResponseWrapper<GetVaultIdResponse>>();
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