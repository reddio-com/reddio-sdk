package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Signature {
    @JsonProperty("r")
    private String r;
    @JsonProperty("s")
    private String s;

    @JsonProperty("stark_key")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String starkKey;

    public static Signature of(String r, String s) {
        return of(r, s, "");
    }
}
