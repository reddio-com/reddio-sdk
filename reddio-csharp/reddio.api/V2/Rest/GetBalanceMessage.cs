using Newtonsoft.Json;

namespace reddio.api.V2.Rest
{
    public class GetBalanceMessage
    {
        public GetBalanceMessage(string starkKey, string? limit, string? page, string? contractAddress)
        {
            StarkKey = starkKey;
            Limit = limit;
            Page = page;
            ContractAddress = contractAddress;
        }
        [JsonProperty("stark_key")] public string StarkKey { get; set; }
        [JsonProperty("limit")] public string? Limit { get; set; }
        [JsonProperty("page")] public string? Page { get; set; }
        [JsonProperty("contract_address")] public string? ContractAddress { get; set; }
    }
}