using System;
using Newtonsoft.Json;

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

        [JsonProperty("nonce")] public Int64 Nonce { get; set; }
    }
}