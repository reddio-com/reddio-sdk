using System.Numerics;
using System.Text.Json.Serialization;

namespace Reddio.Api.Rest.V1;

public class TransferMessage
{
    [JsonPropertyName("token_id")] public String TokenID;
    [JsonPropertyName("stark_key")] public String StarkKey;
    [JsonPropertyName("amount")] public String Amount;
    [JsonPropertyName("nonce")] public Int64 Nonce;
    [JsonPropertyName("vault_id")] public String VaultID;
    [JsonPropertyName("receiver")] public String Receiver;

    [JsonPropertyName("receiver_vault_id")]
    public String ReceiverVaultID;

    [JsonPropertyName("expiration_timestamp")]
    public Int64 ExpirationTimestamp;

    [JsonPropertyName("signature")] public Signature Signature;
}

public class Signature
{
    [JsonPropertyName("r")] public String R;
    [JsonPropertyName("s")] public String S;
}