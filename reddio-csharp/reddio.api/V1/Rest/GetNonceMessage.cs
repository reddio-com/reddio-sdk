using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetNonceMessage
    {
        [JsonProperty("stark_key")] public string StarkKey { get; set; }

        public GetNonceMessage()
        {
        }

        public GetNonceMessage(string starkKey)
        {
            StarkKey = starkKey;
        }
    }
}