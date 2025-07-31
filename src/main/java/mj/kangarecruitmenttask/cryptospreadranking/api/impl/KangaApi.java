package mj.kangarecruitmenttask.cryptospreadranking.api.impl;

import mj.kangarecruitmenttask.cryptospreadranking.api.MarketApi;
import mj.kangarecruitmenttask.cryptospreadranking.dto.MarketPairDto;
import mj.kangarecruitmenttask.cryptospreadranking.dto.OrderBookDto;
import mj.kangarecruitmenttask.cryptospreadranking.exception.MarketApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class KangaApi implements MarketApi {

    @Value("${kanga.api.pairs.endpoint}")
    private String PAIRS_ENDPOINT;

    @Value("${kanga.api.orderbook.endpoint}")
    private String ORDERBOOK_ENDPOINT;

    private final RestTemplate restTemplate;

    private static final Logger log = LoggerFactory.getLogger(KangaApi.class);

    public KangaApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "availablePairs")
    public List<MarketPairDto> fetchAvailablePairs() {
        try {
            log.info("Request for available pairs");
            MarketPairDto[] marketPairDtos = restTemplate.getForObject(PAIRS_ENDPOINT, MarketPairDto[].class);
            return marketPairDtos != null ? Arrays.asList(marketPairDtos) : List.of();
        } catch (RestClientException e) {
            log.error("Failed to fetch available pairs from {}: {}", PAIRS_ENDPOINT, e.getMessage());
            throw new MarketApiException(e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "orderBook")
    public OrderBookDto fetchOrderBook(String pair) {
        try {
            log.info("Request for order book: {}", pair);
            return restTemplate.getForObject(ORDERBOOK_ENDPOINT + pair, OrderBookDto.class);
        } catch (RestClientException e) {
            log.error("Failed to fetch order book for {}: {}", pair, e.getMessage());
            throw new MarketApiException(e.getMessage());
        }
    }
}
