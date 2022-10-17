using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class GetAssetIdMessage
{
    [JsonPropertyName("contract_address")] public String ContractAddress;
    [JsonPropertyName("type")] public String Type;
    [JsonPropertyName("token_id")] public String TokenId;

    public GetAssetIdMessage()
    {
    }

    public GetAssetIdMessage(string contractAddress, string type, string tokenId)
    {
        ContractAddress = contractAddress;
        Type = type;
        TokenId = tokenId;
    }


}