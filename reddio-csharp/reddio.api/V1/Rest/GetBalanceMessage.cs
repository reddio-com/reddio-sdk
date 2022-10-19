using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetBalanceMessage
    {
        public GetBalanceMessage()
        {
        }

        public GetBalanceMessage(string starkKey, string assetId)
        {
            StarkKey = starkKey;
            AssetId = assetId;
        }

        [JsonProperty("stark_key")] public string StarkKey { get; set; }
        [JsonProperty("asset_id")] public string AssetId { get; set; }
    }
}