using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class OrderResponse
    {
        [JsonProperty("sequence_id")] public string SequenceID;

        public OrderResponse(string sequenceId)
        {
            SequenceID = sequenceId;
        }

        public OrderResponse()
        {
        }
    }
}