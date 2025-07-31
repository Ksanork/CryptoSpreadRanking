package mj.kangarecruitmenttask.cryptospreadranking.service;

import mj.kangarecruitmenttask.cryptospreadranking.dto.RankingDto;
import mj.kangarecruitmenttask.cryptospreadranking.exception.MarketApiException;

public interface SpreadRankingService {
    RankingDto getRanking();
    void calculateRanking() throws MarketApiException;
}
