using System;
using Newtonsoft.Json;

namespace reddio.api.V2.Rest
{
    public class ResponseWrapper<T>
    {
        [JsonProperty("status")] public string Status { get; set; }
        [JsonProperty("error")] public string Error { get; set; }
        [JsonProperty("error_code")] public Int64 ErrorCode { get; set; }
        [JsonProperty("data")] public T Data { get; set; }
    }
}