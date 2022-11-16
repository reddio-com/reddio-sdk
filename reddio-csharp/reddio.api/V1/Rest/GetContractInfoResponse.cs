using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetContractInfoResponse
    {
        [JsonProperty("quantum")] public long Quantum;
        [JsonProperty("count")] public long Count;
        [JsonProperty("type")] public string Type;
        [JsonProperty("decimals")] public string Decimals;
        [JsonProperty("symbol")] public string Symbol;
        [JsonProperty("total_supply")] public string TotalSupply;
        [JsonProperty("asset_type")] public string AssetType;
        [JsonProperty] public string AssetInfo;
        [JsonProperty("id")] public string Id;
        [JsonProperty("belongs_to")] public string BelongsTo;
        [JsonProperty("contract_uuid")] public string ContractUuid;
        [JsonProperty("chain_status")] public string ChainStatus;
        [JsonProperty("contract_address")] public string ContractAddress;

        public GetContractInfoResponse(long quantum, long count, string type, string decimals, string symbol,
            string totalSupply, string assetType, string assetInfo, string id, string belongsTo, string contractUuid,
            string chainStatus, string contractAddress)
        {
            Quantum = quantum;
            Count = count;
            Type = type;
            Decimals = decimals;
            Symbol = symbol;
            TotalSupply = totalSupply;
            AssetType = assetType;
            AssetInfo = assetInfo;
            Id = id;
            BelongsTo = belongsTo;
            ContractUuid = contractUuid;
            ChainStatus = chainStatus;
            ContractAddress = contractAddress;
        }

        public GetContractInfoResponse()
        {
        }
    }
}