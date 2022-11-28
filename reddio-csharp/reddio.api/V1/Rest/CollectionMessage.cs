using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class CollectionMessage
    {
        [JsonProperty("stark_key")] public string StarkKey;
        [JsonProperty("contract_address")] public string ContractAddress;
        [JsonProperty("token_ids")] public string[]? TokenIds;


        public CollectionMessage()
        {
        }

        public CollectionMessage(string starkKey, string contractAddress, string[]? tokenIds)
        {
            StarkKey = starkKey;
            ContractAddress = contractAddress;
            TokenIds = tokenIds;
        }
    }
}