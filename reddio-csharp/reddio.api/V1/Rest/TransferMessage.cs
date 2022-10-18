using System;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class TransferMessage
    {
        [JsonProperty("asset_id")] public string AssetId { get; set; }
        [JsonProperty("stark_key")] public string StarkKey { get; set; }
        [JsonProperty("amount")] public string Amount { get; set; }
        [JsonProperty("nonce")] public Int64 Nonce { get; set; }
        [JsonProperty("vault_id")] public string VaultId { get; set; }
        [JsonProperty("receiver")] public string Receiver { get; set; }

        [JsonProperty("receiver_vault_id")]
        public string ReceiverVaultId { get; set; }

        [JsonProperty("expiration_timestamp")]
        public Int64 ExpirationTimestamp { get; set; }

        [JsonProperty("signature")] public Signature Signature { get; set; }

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