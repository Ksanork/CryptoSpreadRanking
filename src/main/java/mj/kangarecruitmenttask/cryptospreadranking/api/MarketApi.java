package mj.kangarecruitmenttask.cryptospreadranking.api;

import mj.kangarecruitmenttask.cryptospreadranking.dto.OrderBookDto;
import mj.kangarecruitmenttask.cryptospreadranking.dto.MarketPairDto;
import mj.kangarecruitmenttask.cryptospreadranking.exception.MarketApiException;

import java.util.List;

public interface MarketApi {
    List<MarketPairDto> fetchAvailablePairs() throws MarketApiException;
    OrderBookDto fetchOrderBook(String pair) throws MarketApiException;
}
