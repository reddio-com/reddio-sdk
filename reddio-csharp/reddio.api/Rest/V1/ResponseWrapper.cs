using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class ResponseWrapper<T>
{
    [JsonPropertyName("status")]
    public String Status;
    [JsonPropertyName("error")]
    public String Error;
    [JsonPropertyName("data")]
    public T Data;
}