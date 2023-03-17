package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SequenceRecord {
    public static final int SEQUENCE_STATUS_SUBMITTED = 0;
    public static final int SEQUENCE_STATUS_ACCEPTED = 1;
    public static final int SEQUENCE_STATUS_FAILED = 2;
    public static final int SEQUENCE_STATUS_PROVED = 3;
    public static final int SEQUENCE_STATUS_PROVED_ERROR = 4;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("asset_id")
    private String assetId;

    @JsonProperty("asset_name")
    private String assetName;

    @JsonProperty("asset_type")
    private String assetType;

    @JsonProperty("contract_address")
    private String contractAddress;

    @JsonProperty("display_value")
    private String displayValue;

    @JsonProperty("from")
    private String from;

    @JsonProperty("order")
    private Order order;

    @JsonProperty("record_type")
    private RecordType recordType;

    @JsonProperty("sequence_id")
    private Long sequenceId;

    @JsonProperty("stark_key")
    private String starkKey;

    @JsonProperty("status")
    private RecordStatus status;

    @JsonProperty("resp")
    private String resp;

    @JsonProperty("time")
    private Long time;

    @JsonProperty("to")
    private String to;

    @JsonProperty("token_id")
    private String tokenId;

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
