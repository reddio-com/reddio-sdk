using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class GetNonceResponse
{
    [JsonPropertyName("nonce")]
    public Int64 Nonce;
}