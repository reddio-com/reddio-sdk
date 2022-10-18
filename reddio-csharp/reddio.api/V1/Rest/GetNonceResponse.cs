using System;
using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest
{
    public class GetNonceResponse
    {
        public GetNonceResponse()
        {
        }

        public GetNonceResponse(long nonce)
        {
            Nonce = nonce;
        }

        [JsonPropertyName("nonce")] public Int64 Nonce { get; set; }
    }
}