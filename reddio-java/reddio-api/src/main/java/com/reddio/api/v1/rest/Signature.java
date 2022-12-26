package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Signature {
    @JsonProperty("r")
    public String r;
    @JsonProperty("s")
    public String s;

    @JsonProperty("stark_key")
    public String starkKey;

    public static Signature of(String r, String s) {
        return of(r, s, "");
    }
}
