package mj.kangarecruitmenttask.cryptospreadranking.service;


import mj.kangarecruitmenttask.cryptospreadranking.api.MarketApi;
import mj.kangarecruitmenttask.cryptospreadranking.dto.MarketDataDto;
import mj.kangarecruitmenttask.cryptospreadranking.dto.MarketPairDto;
import mj.kangarecruitmenttask.cryptospreadranking.dto.OrderBookDto;
import mj.kangarecruitmenttask.cryptospreadranking.dto.RankingDto;
import mj.kangarecruitmenttask.cryptospreadranking.exception.MarketApiException;
import mj.kangarecruitmenttask.cryptospreadranking.model.StaticContent;
import mj.kangarecruitmenttask.cryptospreadranking.service.impl.SpreadRankingServiceImpl;
import mj.kangarecruitmenttask.cryptospreadranking.util.SpreadCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SpreadRankingServiceImplTest {

    @Mock
    private MarketApi marketApi;

    @Mock
    private ExecutorService executorService;

    @InjectMocks
    private SpreadRankingServiceImpl service;

    @Test
    void whenSpreadsAreProvidedThenShouldBeCorrectlyGrouped() {
        MarketDataDto md1 = new MarketDataDto("BTC-USD", 1.5);
        MarketDataDto md2 = new MarketDataDto("ETH-USD", 3.0);
        MarketDataDto md3 = new MarketDataDto("DOGE-USD", null);

        service.setSpreadRankingMap(Map.of(
                StaticContent.SPREAD_GROUP_1, List.of(md1),
                StaticContent.SPREAD_GROUP_2, List.of(md2),
                StaticContent.SPREAD_GROUP_3, List.of(md3)
        ));

        RankingDto rankingDto = service.getRanking();

        assertThat(rankingDto).isNotNull();
        assertThat(rankingDto.timestamp()).isNotNull();
        assertThat(rankingDto.ranking().group1()).containsExactly(md1);
        assertThat(rankingDto.ranking().group2()).containsExactly(md2);
        assertThat(rankingDto.ranking().group3()).containsExactly(md3);
    }

    @Test
    void whenPairExistsAndOrderBookHasDataAndSpreadWasCalculatedThenShouldReturnCorrectGroup() throws Exception {
        MarketPairDto pair1 = new MarketPairDto("BTC_USDT");
        OrderBookDto orderBookDto1 = new OrderBookDto("BTC_USDT", List.of(), List.of());

        when(marketApi.fetchAvailablePairs()).thenReturn(List.of(pair1));
        when(marketApi.fetchOrderBook("BTC_USDT")).thenReturn(orderBookDto1);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(executorService).execute(any(Runnable.class));

        Map<String, List<MarketDataDto>> calculatedMap = Map.of(
                StaticContent.SPREAD_GROUP_1, List.of(new MarketDataDto("BTC_USDT", 1.23))
        );

        try (MockedStatic<SpreadCalculator> mockedStatic = mockStatic(SpreadCalculator.class)) {
            mockedStatic.when(() -> SpreadCalculator.calculateSpread(List.of(orderBookDto1)))
                    .thenReturn(calculatedMap);

            service.calculateRanking();

            verify(marketApi).fetchAvailablePairs();
            verify(marketApi).fetchOrderBook("BTC_USDT");

            mockedStatic.verify(() -> SpreadCalculator.calculateSpread(List.of(orderBookDto1)));

            Map<String, List<MarketDataDto>> resultMap = service.getSpreadRankingMap();
            assertThat(resultMap).containsKey(StaticContent.SPREAD_GROUP_1);
            assertThat(resultMap.get(StaticContent.SPREAD_GROUP_1))
                    .extracting(MarketDataDto::spread)
                    .containsExactly(1.23);
        }
    }

    @Test
    void whenPairExistsButOneOfOrderBookHasNoDataThenShouldNotProcessIt() throws Exception {
        MarketPairDto pair1 = new MarketPairDto("BTC_USDT");
        MarketPairDto pair2 = new MarketPairDto("ETH_USDT");

        when(marketApi.fetchAvailablePairs()).thenReturn(List.of(pair1, pair2));

        when(marketApi.fetchOrderBook("BTC_USDT")).thenThrow(new MarketApiException("Orderbook error"));
        OrderBookDto orderBookDto2 = new OrderBookDto("ETH_USDT", List.of(), List.of());
        when(marketApi.fetchOrderBook("ETH_USDT")).thenReturn(orderBookDto2);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(executorService).execute(any(Runnable.class));

        Map<String, List<MarketDataDto>> calculatedMap = Map.of(
                StaticContent.SPREAD_GROUP_1, List.of(new MarketDataDto("ETH_USDT", 2.34))
        );

        try (MockedStatic<SpreadCalculator> mockedStatic = mockStatic(SpreadCalculator.class)) {
            mockedStatic.when(() -> SpreadCalculator.calculateSpread(List.of(orderBookDto2)))
                    .thenReturn(calculatedMap);

            service.calculateRanking();

            verify(marketApi).fetchAvailablePairs();
            verify(marketApi).fetchOrderBook("BTC_USDT");
            verify(marketApi).fetchOrderBook("ETH_USDT");

            mockedStatic.verify(() -> SpreadCalculator.calculateSpread(List.of(orderBookDto2)));

            Map<String, List<MarketDataDto>> resultMap = service.getSpreadRankingMap();
            assertThat(resultMap).containsKey(StaticContent.SPREAD_GROUP_1);
            assertThat(resultMap.get(StaticContent.SPREAD_GROUP_1))
                    .extracting(MarketDataDto::spread)
                    .containsExactly(2.34);
        }
    }

    @Test
    void whenApiErrorOccurredThenItShouldBePropagated() {
        when(marketApi.fetchAvailablePairs()).thenThrow(new MarketApiException("API error"));

        assertThrows(MarketApiException.class, () -> {
            service.calculateRanking();
        });

        verify(marketApi).fetchAvailablePairs();
        verifyNoMoreInteractions(marketApi);
    }


}
