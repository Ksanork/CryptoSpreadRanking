package mj.kangarecruitmenttask.cryptospreadranking.dto;

import java.util.List;

public record RankingGroupDto (
    List<MarketDataDto> group1,
    List<MarketDataDto> group2,
    List<MarketDataDto> group3
) {}
