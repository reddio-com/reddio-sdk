using System.Collections.Generic;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetRecordsResponse
    {
        [JsonProperty] 
        public List<SequenceRecord> List { get; set; }
    }
}