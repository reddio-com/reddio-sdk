using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class GetVaultIdMessage
{
    [JsonPropertyName("asset_id")] public String AssetId { get; set; }
    [JsonPropertyName("stark_keys")] public List<String> StarkKeys { get; set; }

    public GetVaultIdMessage()
    {
    }

    public GetVaultIdMessage(string assetId, List<string> starkKeys)
    {
        AssetId = assetId;
        StarkKeys = starkKeys;
    }
}