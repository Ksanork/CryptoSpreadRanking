package mj.kangarecruitmenttask.cryptospreadranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MarketPairDto(
        @JsonProperty("ticker_id")
        String tickerId
){}
