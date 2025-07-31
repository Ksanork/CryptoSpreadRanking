package mj.kangarecruitmenttask.cryptospreadranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record MarketPairDto(
        @JsonProperty("ticker_id")
        String tickerId
) implements Serializable {}
