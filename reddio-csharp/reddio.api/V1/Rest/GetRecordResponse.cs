using System;
using System.Collections.Generic;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetRecordResponse : List<SequenceRecord>
    {
    }

    public class SequenceRecord
    {
        public const int SequenceStatusSubmitted = 0;
        public const int SequenceStatusAccepted = 1;
        public const int SequenceStatusFailed = 2;
        public const int SequenceStatusProved = 3;
        public const int SequenceStatusProvedError = 4;

        public SequenceRecord(string amount, string assetId, string assetName, string assetType, string contractAddress,
            string displayValue, string from, int recordType, long sequenceId, string starkKey, int status, long time,
            string to, string tokenId)
        {
            Amount = amount;
            AssetId = assetId;
            AssetName = assetName;
            AssetType = assetType;
            ContractAddress = contractAddress;
            DisplayValue = displayValue;
            From = from;
            RecordType = recordType;
            SequenceId = sequenceId;
            StarkKey = starkKey;
            Status = status;
            Time = time;
            To = to;
            TokenId = tokenId;
        }

        public SequenceRecord()
        {
        }

        [JsonProperty("amount")] public string Amount { get; set; }
        [JsonProperty("asset_id")] public string AssetId { get; set; }
        [JsonProperty("asset_name")] public string AssetName { get; set; }
        [JsonProperty("asset_type")] public string AssetType { get; set; }
        [JsonProperty("contract_address")] public string ContractAddress { get; set; }
        [JsonProperty("display_value")] public string DisplayValue { get; set; }
        [JsonProperty("from")] public string From { get; set; }
        [JsonProperty("record_type")] public int RecordType { get; set; }
        [JsonProperty("sequence_id")] public Int64 SequenceId { get; set; }
        [JsonProperty("stark_key")] public string StarkKey { get; set; }
        [JsonProperty("status")] public int Status { get; set; }
        [JsonProperty("time")] public Int64 Time { get; set; }
        [JsonProperty("to")] public string To { get; set; }
        [JsonProperty("token_id")] public string TokenId { get; set; }
    }
}