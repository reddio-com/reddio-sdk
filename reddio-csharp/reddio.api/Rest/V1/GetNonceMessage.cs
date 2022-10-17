using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class GetNonceMessage
{
    [JsonPropertyName("stark_key")] public String StarkKey;
}