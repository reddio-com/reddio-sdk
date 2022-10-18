using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetAssetIdResponse
    {
        [JsonProperty("asset_id")] public string AssetId { get; set; }

        public GetAssetIdResponse()
        {
        }

        public GetAssetIdResponse(string assetId)
        {
            AssetId = assetId;
        }
    }
}