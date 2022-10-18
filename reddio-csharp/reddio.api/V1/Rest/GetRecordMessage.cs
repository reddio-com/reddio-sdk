using System;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetRecordMessage
    {
        public GetRecordMessage()
        {
        }

        public GetRecordMessage(string starkKey, long sequenceId)
        {
            StarkKey = starkKey;
            SequenceId = sequenceId;
        }

        [JsonProperty("stark_key")] public string StarkKey { get; set; }
        [JsonProperty("sequence_id")] public Int64 SequenceId { get; set; }
    }
}