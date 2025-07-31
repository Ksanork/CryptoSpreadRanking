package mj.kangarecruitmenttask.cryptospreadranking.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import mj.kangarecruitmenttask.cryptospreadranking.util.EmptySpreadToStringSerializer;
import mj.kangarecruitmenttask.cryptospreadranking.util.SpreadToStringSerializer;

public record MarketDataDto(
        String market,

        @JsonSerialize(using = SpreadToStringSerializer.class, nullsUsing = EmptySpreadToStringSerializer.class)
        Double spread
) {}
