using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class OrderListMessage
    {
        [JsonProperty("stark_key")] public string StarkKey;
        [JsonProperty("contract_address")] public string ContractAddress;

        public OrderListMessage()
        {
        }

        public OrderListMessage(string starkKey, string contractAddress)
        {
            StarkKey = starkKey;
            ContractAddress = contractAddress;
        }
    }
}