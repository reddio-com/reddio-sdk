using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class GetVaultIdMessage
{
    [JsonPropertyName("asset_id")] public String AssetId;
    [JsonPropertyName("stark_keys")] public String StarkKeys;
}