using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class GetAssetIdResponse
{
    [JsonPropertyName("asset_id")] public String AssetId;
}