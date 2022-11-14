using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetRecordsMessage
    {
        public GetRecordsMessage()
        {
        }

        public GetRecordsMessage(string starkKey)
        {
            StarkKey = starkKey;
        }

        [JsonProperty("stark_key")] public string StarkKey { get; set; }
    }
}