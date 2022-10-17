using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class GetAssetIdResponse
{
    [JsonPropertyName("asset_id")] public String AssetId;
}