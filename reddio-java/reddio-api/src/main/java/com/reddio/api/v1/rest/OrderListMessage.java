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
    public String starkKey;
    @JsonProperty("contract_address")
    public String contractAddress;
    @JsonProperty("limit")
    public Long limit;
    @JsonProperty("page")
    public Long page;
    @JsonProperty("direction")
    public Integer direction;
    @JsonProperty("token_ids")
    public List<Long> tokenIds;

    @JsonProperty("order_state")
    public OrderState orderState;
}
