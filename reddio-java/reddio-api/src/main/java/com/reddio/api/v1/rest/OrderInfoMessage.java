package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class OrderInfoMessage {
    @JsonProperty("stark_key")
    private String starkKey;
    @JsonProperty("contract1")
    private String contract1;
    @JsonProperty("contract2")
    private String contract2;
}
