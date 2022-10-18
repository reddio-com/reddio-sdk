using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class ResponseWrapper<T>
    {
        [JsonProperty("status")] public string Status { get; set; }
        [JsonProperty("error")] public string Error { get; set; }
        [JsonProperty("data")] public T Data { get; set; }

        public ResponseWrapper()
        {
        }

        public ResponseWrapper(string status, string error, T data)
        {
            Status = status;
            Error = error;
            Data = data;
        }
    }
}