package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class OrderListMessage {
    @JsonProperty("stark_key")
    private String starkKey;
    @JsonProperty("contract_address")
    private String contractAddress;
    @JsonProperty("limit")
    private Long limit;
    @JsonProperty("page")
    private Long page;
    @JsonProperty("direction")
    private Integer direction;
    @JsonProperty("token_ids")
    private List<Long> tokenIds;
    @JsonProperty("order_state")
    private OrderState orderState;
}
