using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class GetNonceMessage
{
    [JsonPropertyName("stark_key")] public String StarkKey;
}