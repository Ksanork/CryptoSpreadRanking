package mj.kangarecruitmenttask.cryptospreadranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OrderBookDto(
        @JsonProperty("ticker_id")
        String tickerId,
        List<OrderBookEntryDto> asks,
        List<OrderBookEntryDto> bids
) {}
