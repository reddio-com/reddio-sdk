package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class GetRecordMessage {
    @JsonProperty("stark_key")
    public String starkKey;
    @JsonProperty("sequence_id")
    public Long sequenceId;
}
