using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetContractInfoMessage
    {
        public GetContractInfoMessage()
        {
        }

        public GetContractInfoMessage(string type, string contractAddress)
        {
            Type = type;
            ContractAddress = contractAddress;
        }

        [JsonProperty("type")] public string Type;
        [JsonProperty("contract_address")] public string ContractAddress;
    }
}