using System.Net.Http.Json;

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

    public async Task<ResponseWrapper<TransferResponse>> Transfer(TransferMessage transferMessage)
    {
        var endpoint = $"{_baseEndpoint}/v1/transfer";
        var client = new HttpClient();
        var response = await client.PostAsJsonAsync(endpoint, transferMessage);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ResponseWrapper<TransferResponse>>();
        return result!;
    }

    public async Task<ResponseWrapper<GetNonceResponse>> GetNonce(GetNonceMessage getNonceMessage)
    {
        var endpoint = $"{_baseEndpoint}/v1/nonce?stark_key={getNonceMessage.StarkKey}";
        var client = new HttpClient();
        var response = await client.GetAsync(endpoint);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ResponseWrapper<GetNonceResponse>>();
        return result!;
    }

    public async Task<ResponseWrapper<GetAssetIdResponse>> GetAssetId(GetAssetIdMessage getAssetIdMessage)
    {
        var endpoint =
            $"{_baseEndpoint}/v1/assetid?type={getAssetIdMessage.Type}&contract_address={getAssetIdMessage.ContractAddress}&token_id={getAssetIdMessage.TokenId}";
        var client = new HttpClient();
        var response = await client.GetAsync(endpoint);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ResponseWrapper<GetAssetIdResponse>>();
        return result!;
    }

    public async Task<ResponseWrapper<GetVaultIdResponse>> GetVaultId(GetVaultIdMessage getVaultIdMessage)
    {
        var endpoint =
            $"{_baseEndpoint}/v1/vaults?asset_id={getVaultIdMessage.AssetId}&stark_keys={getVaultIdMessage.StarkKeys}";
        var client = new HttpClient();
        var response = await client.GetAsync(endpoint);
        response.EnsureSuccessStatusCode();
        var result = await response.Content.ReadFromJsonAsync<ResponseWrapper<GetVaultIdResponse>>();
        return result!;
    }

    public static ReddioRestClient MainnetClient()
    {
        return new ReddioRestClient(MainnetApiEndpoint);
    }

    public static ReddioRestClient TestnetClient()
    {
        return new ReddioRestClient(TestnetApiEndpoint);
    }
}