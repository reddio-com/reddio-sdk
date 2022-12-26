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
    public PayInfo payInfo;
    @JsonProperty("state")
    public String state;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    public static class PayInfo {
        @JsonProperty("order_id")
        public String orderId;
    }
}
