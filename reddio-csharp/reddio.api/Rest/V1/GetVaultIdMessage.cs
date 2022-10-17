using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class GetVaultIdMessage
{
    [JsonPropertyName("asset_id")] public String AssetId;
    [JsonPropertyName("stark_keys")] public String StarkKeys;
}