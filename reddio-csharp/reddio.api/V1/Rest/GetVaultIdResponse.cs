using System.Collections.Generic;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class GetVaultIdResponse
    {
        public GetVaultIdResponse()
        {
        }

        public GetVaultIdResponse(List<string> vaultIds)
        {
            VaultIds = vaultIds;
        }

        [JsonProperty("vault_ids")] public List<string> VaultIds { get; set; }
    }
}