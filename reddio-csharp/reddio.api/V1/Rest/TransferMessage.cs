using System;
using System.Numerics;
using System.Text.Json.Serialization;

namespace Reddio.Api.V1.Rest
{
    public class TransferMessage
    {
        [JsonPropertyName("asset_id")] public string AssetId { get; set; }
        [JsonPropertyName("stark_key")] public string StarkKey { get; set; }
        [JsonPropertyName("amount")] public string Amount { get; set; }
        [JsonPropertyName("nonce")] public Int64 Nonce { get; set; }
        [JsonPropertyName("vault_id")] public string VaultId { get; set; }
        [JsonPropertyName("receiver")] public string Receiver { get; set; }

        [JsonPropertyName("receiver_vault_id")]
        public string ReceiverVaultId { get; set; }

        [JsonPropertyName("expiration_timestamp")]
        public Int64 ExpirationTimestamp { get; set; }

        [JsonPropertyName("signature")] public Signature Signature { get; set; }

        public TransferMessage()
        {
        }

        public TransferMessage(string assetId, string starkKey, string amount, long nonce, string vaultId,
            string receiver, string receiverVaultId, long expirationTimestamp, Signature signature)
        {
            AssetId = assetId;
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
}