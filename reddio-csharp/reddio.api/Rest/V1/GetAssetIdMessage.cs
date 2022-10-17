using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class GetAssetIdMessage
{
    [JsonPropertyName("contract_address")] public String ContractAddress;
    [JsonPropertyName("type")] public String Type;
    [JsonPropertyName("token_id")] public String TokenId;
}