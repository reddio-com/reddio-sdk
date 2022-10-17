using System.Numerics;
using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest;

public class TransferMessage
{
    [JsonPropertyName("token_id")] public String TokenID { get; set; }
    [JsonPropertyName("stark_key")] public String StarkKey { get; set; }
    [JsonPropertyName("amount")] public String Amount { get; set; }
    [JsonPropertyName("nonce")] public Int64 Nonce { get; set; }
    [JsonPropertyName("vault_id")] public String VaultId { get; set; }
    [JsonPropertyName("receiver")] public String Receiver { get; set; }

    [JsonPropertyName("receiver_vault_id")]
    public String ReceiverVaultId { get; set; }

    [JsonPropertyName("expiration_timestamp")]
    public Int64 ExpirationTimestamp { get; set; }

    [JsonPropertyName("signature")] public Signature Signature { get; set; }

    public TransferMessage()
    {
    }

    public TransferMessage(string tokenId, string starkKey, string amount, long nonce, string vaultId, string receiver,
        string receiverVaultId, long expirationTimestamp, Signature signature)
    {
        TokenID = tokenId;
        StarkKey = starkKey;
        Amount = amount;
        Nonce = nonce;
        VaultId = vaultId;
        Receiver = receiver;
        ReceiverVaultId = receiverVaultId;
        ExpirationTimestamp = expirationTimestamp;
        Signature = signature;
    }
}

public class Signature
{
    [JsonPropertyName("r")] public String R;
    [JsonPropertyName("s")] public String S;

    public Signature(string r, string s)
    {
        R = r;
        S = s;
    }
}