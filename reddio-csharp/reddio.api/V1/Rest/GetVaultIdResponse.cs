using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class GetVaultIdResponse
{
    [JsonPropertyName("vault_id")] public Int64 VaultId;
}