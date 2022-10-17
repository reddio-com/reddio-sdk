using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class TransferResponse
{
    [JsonPropertyName("sequence_id")]
    public Int64 SequenceId;
}