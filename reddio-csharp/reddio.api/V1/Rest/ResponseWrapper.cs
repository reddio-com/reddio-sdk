using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class ResponseWrapper<T>
{
    [JsonPropertyName("status")] public String Status { get; set; }
    [JsonPropertyName("error")] public String Error { get; set; }
    [JsonPropertyName("data")] public T Data { get; set; }

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