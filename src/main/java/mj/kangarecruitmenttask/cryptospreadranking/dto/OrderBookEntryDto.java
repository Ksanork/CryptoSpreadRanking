package mj.kangarecruitmenttask.cryptospreadranking.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import mj.kangarecruitmenttask.cryptospreadranking.util.OrderBookEntryDeserializer;

@JsonDeserialize(using = OrderBookEntryDeserializer.class)
public record OrderBookEntryDto(
        double price,
        double quantity
) {}


