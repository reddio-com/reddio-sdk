package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetContractInfoMessage {
    @JsonProperty("type")
    public String type;
    @JsonProperty("contract_address")
    public String contractAddress;
}
