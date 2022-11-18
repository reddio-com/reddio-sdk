using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetBalancesMessage
    {
        public GetBalancesMessage(string starkKey, string contractAddress)
        {
            StarkKey = starkKey;
            ContractAddress = contractAddress;
        }

        public GetBalancesMessage(string starkKey)
        {
            StarkKey = starkKey;
        }

        public GetBalancesMessage()
        {
        }

        [JsonProperty("stark_key")] public string StarkKey { get; set; }
        [JsonProperty("contract_address")] public string ContractAddress { get; set; }
    }
}