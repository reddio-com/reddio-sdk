using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class GetNonceResponse
{
    [JsonPropertyName("nonce")]
    public Int64 Nonce;
}