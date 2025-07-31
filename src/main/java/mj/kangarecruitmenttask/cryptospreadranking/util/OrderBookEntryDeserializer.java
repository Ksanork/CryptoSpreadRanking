package mj.kangarecruitmenttask.cryptospreadranking.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import mj.kangarecruitmenttask.cryptospreadranking.dto.OrderBookEntryDto;

import java.io.IOException;

public class OrderBookEntryDeserializer extends JsonDeserializer<OrderBookEntryDto> {

    @Override
    public OrderBookEntryDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ArrayNode node = p.getCodec().readTree(p);
        return new OrderBookEntryDto(node.get(0).asDouble(), node.get(1).asDouble());
    }
}

