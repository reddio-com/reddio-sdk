using Newtonsoft.Json;

namespace reddio.api.V2.Rest
{
    public class GetBalanceMessage
    {
        public GetBalanceMessage(string starkKey,  string? contractAddress)
        {
            StarkKey = starkKey;

            ContractAddress = contractAddress;
        }
        [JsonProperty("stark_key")] public string StarkKey { get; set; }
        [JsonProperty("contract_address")] public string? ContractAddress { get; set; }
    }
}