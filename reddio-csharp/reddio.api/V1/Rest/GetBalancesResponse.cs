using System.Collections.Generic;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetBalancesResponse
    {
        [JsonProperty("list")] public List<Balance> list;

        public GetBalancesResponse(List<Balance> list)
        {
            this.list = list;
        }

        public GetBalancesResponse()
        {
        }
    }
}