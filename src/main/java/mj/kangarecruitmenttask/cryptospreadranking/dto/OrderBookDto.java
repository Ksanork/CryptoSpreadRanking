package mj.kangarecruitmenttask.cryptospreadranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public record OrderBookDto(
        @JsonProperty("ticker_id")
        String tickerId,
        List<OrderBookEntryDto> asks,
        List<OrderBookEntryDto> bids
) implements Serializable {}
