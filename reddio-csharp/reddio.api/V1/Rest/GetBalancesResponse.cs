using System.Collections.Generic;

namespace Reddio.Api.V1.Rest
{
    public class GetBalancesResponse : List<Balance>
    {
        public GetBalancesResponse()
        {
        }

        public GetBalancesResponse(IEnumerable<Balance> collection) : base(collection)
        {
        }
    }
}