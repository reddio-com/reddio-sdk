using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class OrderResponse
    {
        [JsonProperty("sequence_id")] public string SequenceId;

        public OrderResponse(string sequenceId)
        {
            SequenceId = sequenceId;
        }

        public OrderResponse()
        {
        }
    }
}