package mj.kangarecruitmenttask.cryptospreadranking.model;

public record SpreadData(
        double spread,
        String tickerId,
        boolean invalidData
) {}
