package mj.kangarecruitmenttask.cryptospreadranking.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import mj.kangarecruitmenttask.cryptospreadranking.dto.MarketDataDto;
import mj.kangarecruitmenttask.cryptospreadranking.dto.OrderBookDto;
import mj.kangarecruitmenttask.cryptospreadranking.config.StaticContent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class SpreadCalculatorTest {

    ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void whenEmptyOrderBookListIsEmptyThenShouldReturnEmptyMap() {
        Map<String, List<MarketDataDto>> result = SpreadCalculator.calculateSpread(List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void whenOrderBookHasAskAndBidsAndSpreadHasMoreThan2PctThenITShouldAppearInGroup2() throws IOException {
        OrderBookDto orderbookDto = readOrderBookAsset("orderbook_response.json");

        Map<String, List<MarketDataDto>> spreadMap = SpreadCalculator.calculateSpread(List.of(orderbookDto));

        assertThat(spreadMap.get("group1")).isNull();
        assertThat(spreadMap.get("group2")).isNotEmpty();
        assertThat(spreadMap.get("group3")).isNull();
        assertThat(spreadMap.get("group2").getFirst().spread()).isEqualTo(7.644994187818122);
    }

    @Test
    void whenOrderbookHasNotBidsTenITShouldAppearInGroup3() throws IOException {
        OrderBookDto orderbookDto = readOrderBookAsset("orderbook_response_empty_bids.json");

        Map<String, List<MarketDataDto>> spreadMap = SpreadCalculator.calculateSpread(List.of(orderbookDto));

        assertThat(spreadMap.get("group1")).isNull();
        assertThat(spreadMap.get("group2")).isNull();
        assertThat(spreadMap.get("group3")).isNotEmpty();
        assertThat(spreadMap.get("group3").getFirst().spread()).isNull();
    }

    @Test
    void whenOrderBookHasManyBidsAndAsksShouldCorrectCalculateSpread() throws IOException {
        OrderBookDto orderbookDto = readOrderBookAsset("orderbook_response_many_asks_and_bids.json");

        Map<String, List<MarketDataDto>> spreadMap = SpreadCalculator.calculateSpread(List.of(orderbookDto));

        assertThat(spreadMap.get("group1")).isNotEmpty();
        assertThat(spreadMap.get("group2")).isNull();
        assertThat(spreadMap.get("group3")).isNull();
        assertThat(spreadMap.get("group1").getFirst().spread()).isEqualTo(0.5097565206051884);
    }

    @Test
    void whenMultipleOrderBooksProvidedThenShouldProcessAll() throws IOException {
        OrderBookDto orderbook1 = readOrderBookAsset("orderbook_response.json"); // group2
        OrderBookDto orderbook2 = readOrderBookAsset("orderbook_response_many_asks_and_bids.json"); // group1
        OrderBookDto orderbook3 = readOrderBookAsset("orderbook_response_empty_bids.json"); // group3

        Map<String, List<MarketDataDto>> result = SpreadCalculator.calculateSpread(List.of(orderbook1, orderbook2, orderbook3));

        assertThat(result.get(StaticContent.SPREAD_GROUP_1)).isNotEmpty();
        assertThat(result.get(StaticContent.SPREAD_GROUP_2)).isNotEmpty();
        assertThat(result.get(StaticContent.SPREAD_GROUP_3)).isNotEmpty();
    }

    @Test
    void whenOrderBookHasEmptyBidsAndAsksThenShouldAppearInGroup3() {
        OrderBookDto emptyOrderbook = new OrderBookDto("TEST_TICKER", List.of(), List.of());
        Map<String, List<MarketDataDto>> spreadMap = SpreadCalculator.calculateSpread(List.of(emptyOrderbook));

        assertThat(spreadMap.get(StaticContent.SPREAD_GROUP_1)).isNull();
        assertThat(spreadMap.get(StaticContent.SPREAD_GROUP_2)).isNull();
        assertThat(spreadMap.get(StaticContent.SPREAD_GROUP_3)).isNotEmpty();
        assertThat(spreadMap.get(StaticContent.SPREAD_GROUP_3).getFirst().spread()).isNull();
    }

    private OrderBookDto readOrderBookAsset(String asset) throws IOException {
        return mapper.readValue(getClass().getClassLoader().getResource(asset), OrderBookDto.class);
    }
}
