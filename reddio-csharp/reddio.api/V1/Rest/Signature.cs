using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class Signature
{
    [JsonPropertyName("r")] public String R { get; set; }
    [JsonPropertyName("s")] public String S { get; set; }

    public Signature(string r, string s)
    {
        R = r;
        S = s;
    }
}