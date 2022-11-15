using System;
using System.Collections.Generic;
using Newtonsoft.Json;


namespace reddio.api.V2.Rest
{
    public class GetBalanceResponse : List<Balance>
    {
    }

    public class Balance
    {
        [JsonProperty("asset_id")] public string AssetId { get; set; }
        [JsonProperty("contract_address")] public string ContractAddress { get; set; }
        [JsonProperty("balance_available")] public Int64 BalanceAvailable { get; set; }
        [JsonProperty("balance_frozen")] public Int64 BalanceFrozen { get; set; }
        [JsonProperty("type")] public string Type { get; set; }
        [JsonProperty("decimals")] public Int64 Decimals { get; set; }
        [JsonProperty("symbol")] public string Symbol { get; set; }
        [JsonProperty("quantum")] public Int64 Quantum { get; set; }
        [JsonProperty("display_value")] public string DisplayValue { get; set; }
        [JsonProperty("display_frozen")] public string DispalyFrozen { get; set; }
        [JsonProperty("available_token_ids")] public List<String> AvailableTokenIds { get; set; }
    }
}