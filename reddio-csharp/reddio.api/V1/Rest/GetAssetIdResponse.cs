using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest
{
    public class GetAssetIdResponse
    {
        [JsonPropertyName("asset_id")] public string AssetId { get; set; }

        public GetAssetIdResponse()
        {
        }

        public GetAssetIdResponse(string assetId)
        {
            AssetId = assetId;
        }
    }
}