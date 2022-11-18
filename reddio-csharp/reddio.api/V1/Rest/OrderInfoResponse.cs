using System.Collections.Generic;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class OrderInfoResponse
    {
        public OrderInfoResponse(List<Contract> contracts, List<string> vaultIDs, List<string> assetIDs, string feeRate,
            string baseToken, string feeToken, long lowerLimit, long nonce)
        {
            Contracts = contracts;
            VaultIDs = vaultIDs;
            AssetIDs = assetIDs;
            FeeRate = feeRate;
            BaseToken = baseToken;
            FeeToken = feeToken;
            LowerLimit = lowerLimit;
            Nonce = nonce;
        }

        public OrderInfoResponse()
        {
        }

        [JsonProperty("fee_rate")] public string FeeRate { get; set; }
        [JsonProperty("base_token")] public string BaseToken { get; set; }
        [JsonProperty("fee_token")] public string FeeToken { get; set; }
        [JsonProperty("lower_limit")] public long LowerLimit { get; set; }
        [JsonProperty("nonce")] public long Nonce { get; set; }
        [JsonProperty("contracts")] public List<Contract> Contracts;
        [JsonProperty("vault_ids")] public List<string> VaultIDs;
        [JsonProperty("asset_ids")] public List<string> AssetIDs;
    }
}