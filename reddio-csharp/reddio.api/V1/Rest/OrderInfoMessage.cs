using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class OrderInfoMessage
    {
        public OrderInfoMessage(string starkKey, string contract1, string contract2)
        {
            StarkKey = starkKey;
            Contract1 = contract1;
            Contract2 = contract2;
        }

        public OrderInfoMessage()
        {
        }

        [JsonProperty("stark_key")] public string StarkKey { get; set; }
        [JsonProperty("contract1")] public string Contract1 { get; set; }
        [JsonProperty("contract2")] public string Contract2 { get; set; }
    }
}