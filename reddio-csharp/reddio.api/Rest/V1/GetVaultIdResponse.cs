using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class GetVaultIdResponse
{
    [JsonPropertyName("vault_id")] public Int64 VaultId;
}