using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class OrderMessage
    {
        public const long DIRECTION_ASK = 0;
        public const long DIRECTION_BID = 1;

        public OrderMessage(string amount, string amountBuy, string amountSell, string tokenBuy, string tokenSell,
            string baseToken, string quoteToken, string vaultIdBuy, string vaultIdSell, long expirationTimestamp,
            long nonce, Signature signature, string accountId, long direction, FeeInfo feeInfo, string price,
            string starkKey)
        {
            Amount = amount;
            AmountBuy = amountBuy;
            AmountSell = amountSell;
            TokenBuy = tokenBuy;
            TokenSell = tokenSell;
            BaseToken = baseToken;
            QuoteToken = quoteToken;
            VaultIdBuy = vaultIdBuy;
            VaultIdSell = vaultIdSell;
            ExpirationTimestamp = expirationTimestamp;
            Nonce = nonce;
            Signature = signature;
            AccountId = accountId;
            Direction = direction;
            FeeInfo = feeInfo;
            Price = price;
            StarkKey = starkKey;
        }

        public OrderMessage()
        {
        }

        [JsonProperty("amount")] public string Amount;
        [JsonProperty("amount_buy")] public string AmountBuy;
        [JsonProperty("amount_sell")] public string AmountSell;
        [JsonProperty("token_buy")] public string TokenBuy;
        [JsonProperty("token_sell")] public string TokenSell;
        [JsonProperty("base_token")] public string BaseToken;
        [JsonProperty("quote_token")] public string QuoteToken;
        [JsonProperty("vault_id_buy")] public string VaultIdBuy;
        [JsonProperty("vault_id_sell")] public string VaultIdSell;
        [JsonProperty("expiration_timestamp")] public long ExpirationTimestamp;
        [JsonProperty("nonce")] public long Nonce;
        [JsonProperty("signature")] public Signature Signature;
        [JsonProperty("account_id")] public string AccountId;
        [JsonProperty("direction")] public long Direction;
        [JsonProperty("fee_info")] public FeeInfo FeeInfo;
        [JsonProperty("price")] public string Price;
        [JsonProperty("stark_key")] public string StarkKey;
    }
}