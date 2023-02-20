package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ListRecordsMessage {

    @JsonProperty("stark_key")
    private String starkKey;

    @JsonProperty("limit")
    private Long limit;

    @JsonProperty("page")
    private Long page;

    @JsonProperty("contract_address")
    private String contractAddress;

    @JsonProperty("sequence_ids")
    private List<Long> sequenceIds;

    public static ListRecordsMessage of(String starkKey, Long limit, Long page, String contractAddress) {
        return new ListRecordsMessage(starkKey, limit, page, contractAddress, null);
    }

    public static ListRecordsMessage of(List<Long> sequenceIds) {
        return new ListRecordsMessage(null, null, null, null, sequenceIds);
    }
}
