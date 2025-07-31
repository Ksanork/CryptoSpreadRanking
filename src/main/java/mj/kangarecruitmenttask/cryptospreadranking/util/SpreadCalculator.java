package mj.kangarecruitmenttask.cryptospreadranking.util;

import mj.kangarecruitmenttask.cryptospreadranking.dto.MarketDataDto;
import mj.kangarecruitmenttask.cryptospreadranking.dto.OrderBookDto;
import mj.kangarecruitmenttask.cryptospreadranking.dto.OrderBookEntryDto;
import mj.kangarecruitmenttask.cryptospreadranking.model.SpreadData;
import mj.kangarecruitmenttask.cryptospreadranking.config.StaticContent;

import java.util.*;

import static java.util.stream.Collectors.*;

public class SpreadCalculator {

    public static Map<String, List<MarketDataDto>> calculateSpread(List<OrderBookDto> orderBookDtoList) {
        List<SpreadData> spreadData = orderBookDtoList.stream()
                .parallel()
                .map(SpreadCalculator::mapSpreadData)
                .toList();

        return spreadData.stream()
                .map(entry -> {
                    if (entry.invalidData())
                        return Map.entry(StaticContent.SPREAD_GROUP_3, new MarketDataDto(entry.tickerId(), null));
                    if (entry.spread() <= 2.0)
                        return Map.entry(StaticContent.SPREAD_GROUP_1, new MarketDataDto(entry.tickerId(), entry.spread()));
                    return Map.entry(StaticContent.SPREAD_GROUP_2, new MarketDataDto(entry.tickerId(), entry.spread()));
                })
                .collect(groupingByConcurrent(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())));
    }

    private static SpreadData mapSpreadData(OrderBookDto orderbookDto) {
        Optional<Double> maybeHighestAsk = orderbookDto.asks().stream()
                .map(OrderBookEntryDto::price)
                .max(Double::compare);

        Optional<Double> maybeLowestBid = orderbookDto.bids().stream()
                .map(OrderBookEntryDto::price)
                .min(Double::compare);

        if (maybeHighestAsk.isEmpty() || maybeLowestBid.isEmpty()) {
            return new SpreadData(0.0, orderbookDto.tickerId(), true);
        }

        double highestAsk = maybeHighestAsk.get();
        double lowestBid = maybeLowestBid.get();
        double spread = (highestAsk - lowestBid) / (0.5 * (highestAsk + lowestBid)) * 100;
        return new SpreadData(spread, orderbookDto.tickerId(), false);
    }
}
