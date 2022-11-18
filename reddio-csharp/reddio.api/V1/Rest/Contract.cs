using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class Contract
    {
        [JsonProperty("contract_address")] public string ContractAddress;
        [JsonProperty("symbol")] public string Symbol;
        [JsonProperty("decimals")] public long Decimals;
        [JsonProperty("type")] public string Type;
        [JsonProperty("quantum")] public string Quantum;
        [JsonProperty("asset_type")] public string AssetType;

        public Contract(string contractAddress, string symbol, long decimals, string type, string quantum, string assetType)
        {
            ContractAddress = contractAddress;
            Symbol = symbol;
            Decimals = decimals;
            Type = type;
            Quantum = quantum;
            AssetType = assetType;
        }

        public Contract()
        {
        }
    }
}