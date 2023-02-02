package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class WithdrawalStatusMessage {
    public static final String STAGE_WITHDRAWAREA = "withdrawarea";
    @JsonProperty("stage")
    public String stage;
    @JsonProperty("ethaddress")
    public String ethAddress;
}
