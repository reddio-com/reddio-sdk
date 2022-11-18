using System.Collections.Generic;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class OrderListResponse
    {
        public OrderListResponse(List<Order> list, long total)
        {
            List = list;
            Total = total;
        }

        [JsonProperty("list")] public List<Order> List { get; set; }
        [JsonProperty("total")] public long Total { get; set; }
    }

    public class Order
    {
        [JsonProperty("order_id")] public long OrderId;
        [JsonProperty("stark_key")] public string StarkKey;
        [JsonProperty("price")] public string Price;
        [JsonProperty("direction")] public long Direction;
        [JsonProperty("amount")] public string Amount;
        [JsonProperty("un_filled")] public string UnFilled;
        [JsonProperty("symbol")] public Symbol Symbol;
        [JsonProperty("fee_rate")] public string FeeRate;
        [JsonProperty("token_type")] public string TokenType;
        [JsonProperty("token_id")] public string TokenId;
        [JsonProperty("display_price")] public string DisplayPrice;

        public Order(long orderId, string starkKey, string price, long direction, string amount, string unFilled,
            Symbol symbol, string feeRate, string tokenType, string tokenId, string displayPrice)
        {
            OrderId = orderId;
            StarkKey = starkKey;
            Price = price;
            Direction = direction;
            Amount = amount;
            UnFilled = unFilled;
            Symbol = symbol;
            FeeRate = feeRate;
            TokenType = tokenType;
            TokenId = tokenId;
            DisplayPrice = displayPrice;
        }
    }

    public class Symbol
    {
        [JsonProperty("base_token_asset_id")] public string BaseTokenAssetId;
        [JsonProperty("quote_token_asset_id")] public string QuoteTokenAssetId;

        [JsonProperty("base_token_contract_addr")]
        public string BaseTokenContractAddr;

        [JsonProperty("quote_token_contract_addr")]
        public string QuoteTokenContractAddr;

        [JsonProperty("base_token_name")] public string BaseTokenName;
        [JsonProperty("quote_token_name")] public string QuoteTokenName;

        public Symbol(string baseTokenAssetId, string quoteTokenAssetId, string baseTokenContractAddr,
            string quoteTokenContractAddr, string baseTokenName, string quoteTokenName)
        {
            BaseTokenAssetId = baseTokenAssetId;
            QuoteTokenAssetId = quoteTokenAssetId;
            BaseTokenContractAddr = baseTokenContractAddr;
            QuoteTokenContractAddr = quoteTokenContractAddr;
            BaseTokenName = baseTokenName;
            QuoteTokenName = quoteTokenName;
        }
    }
}