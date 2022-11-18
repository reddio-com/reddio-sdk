using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class FeeInfo
    {
        public FeeInfo(long feeLimit, string tokenId, long sourceVaultId)
        {
            FeeLimit = feeLimit;
            TokenId = tokenId;
            SourceVaultId = sourceVaultId;
        }

        public FeeInfo()
        {
        }

        [JsonProperty("fee_limit")] public long FeeLimit;
        [JsonProperty("token_id")] public string TokenId;
        [JsonProperty("source_vault_id")] public long SourceVaultId;
    }
}