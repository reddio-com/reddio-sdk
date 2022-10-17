using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class TransferResponse
{
    [JsonPropertyName("sequence_id")]
    public Int64 SequenceId;
}