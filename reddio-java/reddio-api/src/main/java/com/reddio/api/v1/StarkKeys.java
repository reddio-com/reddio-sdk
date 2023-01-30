package com.reddio.api.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class StarkKeys {
    @JsonProperty("stark_key")
    private String starkKey;
    @JsonProperty("stark_private_key")
    private String starkPrivateKey;
}
