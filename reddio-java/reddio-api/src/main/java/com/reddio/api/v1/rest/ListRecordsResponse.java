package com.reddio.api.v1.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ListRecordsResponse {

    @JsonProperty("list")
    private List<SequenceRecord> list;

    @JsonProperty("total")
    private Long total;

    @JsonProperty("current_page")
    private Long currentPage;

    @JsonProperty("page_size")
    private Long pageSize;

    @JsonProperty("total_page")
    private Long totalPage;
}
