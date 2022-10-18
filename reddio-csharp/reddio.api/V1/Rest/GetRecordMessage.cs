using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

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

    [JsonPropertyName("stark_key")] public string StarkKey { get; set; }
    [JsonPropertyName("sequence_id")] public Int64 SequenceId { get; set; }
}