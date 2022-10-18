using System.Collections.Generic;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetVaultIdMessage
    {
        [JsonProperty("asset_id")] public string AssetId { get; set; }
        [JsonProperty("stark_keys")] public List<string> StarkKeys { get; set; }

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