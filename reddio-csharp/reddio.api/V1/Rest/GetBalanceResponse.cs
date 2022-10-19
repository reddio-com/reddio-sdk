using System;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class Balance
    {
        public Balance(string assetId, string contractAddress, long balanceAvailable, long balanceFrozen, string type,
            long decimals, string symbol, long quantum, string displayValue, string displayFrozen, string tokenId)
        {
            AssetId = assetId;
            ContractAddress = contractAddress;
            BalanceAvailable = balanceAvailable;
            BalanceFrozen = balanceFrozen;
            Type = type;
            Decimals = decimals;
            Symbol = symbol;
            Quantum = quantum;
            DisplayValue = displayValue;
            DisplayFrozen = displayFrozen;
            TokenId = tokenId;
        }

        public Balance()
        {
        }

        [JsonProperty("asset_id")] public string AssetId { get; set; }
        [JsonProperty("contract_address")] public string ContractAddress { get; set; }
        [JsonProperty("balance_available")] public Int64 BalanceAvailable { get; set; }
        [JsonProperty("balance_frozen")] public Int64 BalanceFrozen { get; set; }
        [JsonProperty("type")] public string Type { get; set; }
        [JsonProperty("decimals")] public Int64 Decimals { get; set; }
        [JsonProperty("symbol")] public string Symbol { get; set; }
        [JsonProperty("quantum")] public Int64 Quantum { get; set; }
        [JsonProperty("display_value")] public string DisplayValue { get; set; }
        [JsonProperty("display_frozen")] public string DisplayFrozen { get; set; }
        [JsonProperty("token_id")] public string TokenId { get; set; }
    }

    public class GetBalanceResponse : Balance
    {
        public GetBalanceResponse(string assetId, string contractAddress, long balanceAvailable, long balanceFrozen,
            string type, long decimals, string symbol, long quantum, string displayValue, string displayFrozen,
            string tokenId) : base(assetId, contractAddress, balanceAvailable, balanceFrozen, type, decimals, symbol,
            quantum, displayValue, displayFrozen, tokenId)
        {
        }

        public GetBalanceResponse()
        {
        }
    }
}