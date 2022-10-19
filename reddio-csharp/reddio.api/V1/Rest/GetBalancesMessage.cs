using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetBalancesMessage
    {
        public GetBalancesMessage()
        {
        }

        public GetBalancesMessage(string starkKey)
        {
            StarkKey = starkKey;
        }

        [JsonProperty("stark_key")] public string StarkKey { get; set; }
    }
}