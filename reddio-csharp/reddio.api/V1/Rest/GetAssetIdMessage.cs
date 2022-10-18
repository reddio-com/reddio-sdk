using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetAssetIdMessage
    {
        [JsonProperty("contract_address")] public string ContractAddress { get; set; }
        [JsonProperty("type")] public string Type { get; set; }
        [JsonProperty("token_id")] public string TokenId { get; set; }

        public GetAssetIdMessage()
        {
        }

        public GetAssetIdMessage(string contractAddress, string type, string tokenId)
        {
            ContractAddress = contractAddress;
            Type = type;
            TokenId = tokenId;
        }
    }
}