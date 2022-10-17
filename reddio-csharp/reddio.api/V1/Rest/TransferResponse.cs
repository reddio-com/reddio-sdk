using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class TransferResponse
{
    public TransferResponse()
    {
    }

    public TransferResponse(long sequenceId)
    {
        SequenceId = sequenceId;
    }

    [JsonPropertyName("sequence_id")] public Int64 SequenceId { get; set; }
}