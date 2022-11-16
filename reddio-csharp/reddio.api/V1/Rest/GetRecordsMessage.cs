using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetRecordsMessage
    {
        public GetRecordsMessage()
        {
        }

        public GetRecordsMessage(string starkKey, string? limit, string? page, string? contractAddress)
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