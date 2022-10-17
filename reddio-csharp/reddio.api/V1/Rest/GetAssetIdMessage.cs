using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class GetAssetIdMessage
{
    [JsonPropertyName("contract_address")] public String ContractAddress { get; set; }
    [JsonPropertyName("type")] public String Type { get; set; }
    [JsonPropertyName("token_id")] public String TokenId { get; set; }

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