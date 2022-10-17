using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class ResponseWrapper<T>
{
    [JsonPropertyName("status")]
    public String Status;
    [JsonPropertyName("error")]
    public String Error;
    [JsonPropertyName("data")]
    public T Data;
}