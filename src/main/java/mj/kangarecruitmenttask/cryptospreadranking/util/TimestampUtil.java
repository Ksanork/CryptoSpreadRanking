package mj.kangarecruitmenttask.cryptospreadranking.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampUtil {

    public static String getNowTimestamp() {
        ZonedDateTime utcNow = ZonedDateTime.now(ZoneOffset.UTC);
        return utcNow.format(DateTimeFormatter.ISO_INSTANT);
    }
}
