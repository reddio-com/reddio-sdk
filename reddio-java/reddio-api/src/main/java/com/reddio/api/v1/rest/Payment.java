package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Payment {
    @JsonProperty("pay_info")
    private PayInfo payInfo;
    @JsonProperty("state")
    private String state;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class PayInfo {
        @JsonProperty("order_id")
        private String orderId;
    }
}
