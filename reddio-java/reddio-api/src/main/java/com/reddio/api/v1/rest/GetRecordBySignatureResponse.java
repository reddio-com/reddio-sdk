package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

public class GetRecordBySignatureResponse extends ArrayList<GetRecordBySignatureResponse.Record> {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Record {

        @JsonProperty("amount")
        private String amount;

        @JsonProperty("order")
        private GetRecordBySignatureResponse.Order order;

        @JsonProperty("record_type")
        private Long recordType;

        @JsonProperty("sequence_id")
        private Long sequenceId;

        @JsonProperty("stark_key")
        private String starkKey;

        @JsonProperty("status")
        private int status;

        @JsonProperty("time")
        private Long time;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class Order {

        @JsonProperty("base_asset_id")
        private String baseAssetId;

        @JsonProperty("base_asset_name")
        private String baseAssetName;

        @JsonProperty("base_contract_address")
        private String baseContractAddress;

        @JsonProperty("direction")
        private int direction;

        @JsonProperty("display_price")
        private String displayPrice;

        @JsonProperty("fee_asset_name")
        private String feeAssetName;

        @JsonProperty("fee_taken")
        private String feeTaken;

        @JsonProperty("fee_token_asset")
        private String feeTokenAsset;

        @JsonProperty("filled")
        private String filled;

        @JsonProperty("price")
        private String price;

        @JsonProperty("quote_asset_id")
        private String quoteAssetId;

        @JsonProperty("quote_asset_name")
        private String quoteAssetName;

        @JsonProperty("quote_asset_type")
        private String quoteAssetType;

        @JsonProperty("quote_contract_address")
        private String quoteContractAddress;

        @JsonProperty("token_id")
        private String tokenId;

        @JsonProperty("volume")
        private String volume;
    }
}
