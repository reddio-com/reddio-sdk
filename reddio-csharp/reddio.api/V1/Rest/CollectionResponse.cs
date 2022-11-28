using System.Collections.Generic;
using Newtonsoft.Json;

namespace Reddio.Api.V1.Rest
{
    public class CollectionResponse
    {
        public CollectionResponse(long total, List<CollectionItem> list)
        {
            Total = total;
            List = list;
        }

        public CollectionResponse()
        {
        }

        [JsonProperty("total")] public long Total { get; set; }
        [JsonProperty("list")] public List<CollectionItem> List { get; set; }
    }

    public class CollectionItem
    {
        [JsonProperty("amount")] public long Amount;
        [JsonProperty("price")] public string Price;
        [JsonProperty("display_price")] public string DisplayPrice;
        [JsonProperty("owner")] public string Owner;
        [JsonProperty("token_id")] public string TokenId;

        [JsonProperty("quote_contract_address")]
        public string QuoteContractAddress;

        [JsonProperty("base_contract_address")]
        public string BaseContractAddress;

        public CollectionItem()
        {
        }

        public CollectionItem(long amount, string price, string displayPrice, string owner, string tokenId, string quoteContractAddress, string baseContractAddress)
        {
            Amount = amount;
            Price = price;
            DisplayPrice = displayPrice;
            Owner = owner;
            TokenId = tokenId;
            QuoteContractAddress = quoteContractAddress;
            BaseContractAddress = baseContractAddress;
        }
    }
}