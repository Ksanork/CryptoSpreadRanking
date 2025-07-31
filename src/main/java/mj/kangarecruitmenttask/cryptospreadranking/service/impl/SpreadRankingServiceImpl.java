package mj.kangarecruitmenttask.cryptospreadranking.service.impl;

import lombok.Getter;
import lombok.Setter;
import mj.kangarecruitmenttask.cryptospreadranking.api.MarketApi;
import mj.kangarecruitmenttask.cryptospreadranking.dto.*;
import mj.kangarecruitmenttask.cryptospreadranking.exception.MarketApiException;
import mj.kangarecruitmenttask.cryptospreadranking.model.StaticContent;
import mj.kangarecruitmenttask.cryptospreadranking.service.SpreadRankingService;
import mj.kangarecruitmenttask.cryptospreadranking.util.SpreadCalculator;
import mj.kangarecruitmenttask.cryptospreadranking.util.TimestampUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class SpreadRankingServiceImpl implements SpreadRankingService {

    private final MarketApi marketApi;
    private final ExecutorService orderbookExecutor;

    private static final Logger log = LoggerFactory.getLogger(SpreadRankingServiceImpl.class);

    public SpreadRankingServiceImpl(MarketApi marketApi, ExecutorService orderbookExecutor) {
        this.marketApi = marketApi;
        this.orderbookExecutor = orderbookExecutor;
    }

    @Setter
    @Getter
    private Map<String, List<MarketDataDto>> spreadRankingMap;

    private CompletableFuture<OrderBookDto> createSingleOrderBookFuture(String pair) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return marketApi.fetchOrderBook(pair);
            } catch (MarketApiException e) {
                log.info("Error from API - pair {} omitted", pair);
                return null;
            }
        }, orderbookExecutor);
    }

    @Override
    public RankingDto getRanking() {
        if (spreadRankingMap == null) {
            calculateRanking();
        }

        return mapRanking(TimestampUtil.getNowTimestamp(), spreadRankingMap);
    }

    @Override
    public void calculateRanking() throws MarketApiException {
        log.info("Started calculating ranking");

        List<MarketPairDto> marketPairDtos = marketApi.fetchAvailablePairs();
        log.info("Fetched {} pairs from API", marketPairDtos.size());

        List<CompletableFuture<OrderBookDto>> futures = prepareOrderBookFutures(marketPairDtos);
        List<OrderBookDto> orderBooks = runOrderBookFutures(futures).stream()
                .filter(Objects::nonNull)
                .toList();
        spreadRankingMap = SpreadCalculator.calculateSpread(orderBooks);
    }

    private List<OrderBookDto> runOrderBookFutures(List<CompletableFuture<OrderBookDto>> futures) {
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .join();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private List<CompletableFuture<OrderBookDto>> prepareOrderBookFutures(List<MarketPairDto> marketPairDtos) {
        return marketPairDtos.stream()
                .parallel()
                .map(marketPairDto -> createSingleOrderBookFuture(marketPairDto.tickerId()))
                .toList();
    }

    private RankingDto mapRanking(String timestamp, Map<String, List<MarketDataDto>> spreadDataMap) {
        return new RankingDto(
                timestamp,
                new RankingGroupDto(
                        spreadDataMap.get(StaticContent.SPREAD_GROUP_1),
                        spreadDataMap.get(StaticContent.SPREAD_GROUP_2),
                        spreadDataMap.get(StaticContent.SPREAD_GROUP_3)
                )
        );
    }
}
