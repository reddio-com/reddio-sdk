using System;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class TransferResponse
    {
        public TransferResponse()
        {
        }

        public TransferResponse(long sequenceId)
        {
            SequenceId = sequenceId;
        }

        [JsonProperty("sequence_id")] public Int64 SequenceId { get; set; }
    }
}