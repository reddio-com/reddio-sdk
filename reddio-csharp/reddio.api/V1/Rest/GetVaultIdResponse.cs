using System.Collections.Generic;
using System.Text.Json.Serialization;

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

        [JsonPropertyName("vault_ids")] public List<string> VaultIds { get; set; }
    }
}