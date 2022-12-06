package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;


public class GetRecordResponse extends ArrayList<GetRecordResponse.SequenceRecord> {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class SequenceRecord {

        public static final int SEQUENCE_STATUS_SUBMITTED = 0;
        public static final int SEQUENCE_STATUS_ACCEPTED = 1;
        public static final int SEQUENCE_STATUS_FAILED = 2;
        public static final int SEQUENCE_STATUS_PROVED = 3;
        public static final int SEQUENCE_STATUS_PROVED_ERROR = 4;

        @JsonProperty("amount")
        public String amount;
        @JsonProperty("asset_id")
        public String assetId;
        @JsonProperty("asset_name")
        public String assetName;
        @JsonProperty("asset_type")
        public String assetType;
        @JsonProperty("contract_address")
        public String contractAddress;
        @JsonProperty("display_value")
        public String displayValue;
        @JsonProperty("from")
        public String from;
        @JsonProperty("record_type")
        public String recordType;
        @JsonProperty("sequence_id")
        public long sequenceId;
        @JsonProperty("stark_key")
        public String starkKey;
        @JsonProperty("status")
        public int status;
        @JsonProperty("resp")
        public String resp;
        @JsonProperty("time")
        public long time;
        @JsonProperty("to")
        public String to;
        @JsonProperty("token_id")
        public String tokenId;
    }
}
