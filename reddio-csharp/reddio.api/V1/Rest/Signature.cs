using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class Signature
    {
        [JsonProperty("r")] public string R { get; set; }
        [JsonProperty("s")] public string S { get; set; }

        public Signature(string r, string s)
        {
            R = r;
            S = s;
        }
    }
}