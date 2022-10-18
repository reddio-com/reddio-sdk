using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest
{
    public class GetVaultIdMessage
    {
        [JsonPropertyName("asset_id")] public string AssetId { get; set; }
        [JsonPropertyName("stark_keys")] public List<string> StarkKeys { get; set; }

        public GetVaultIdMessage()
        {
        }

        public GetVaultIdMessage(string assetId, List<string> starkKeys)
        {
            AssetId = assetId;
            StarkKeys = starkKeys;
        }
    }
}