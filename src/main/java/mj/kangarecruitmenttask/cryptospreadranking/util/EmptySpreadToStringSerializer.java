package mj.kangarecruitmenttask.cryptospreadranking.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import mj.kangarecruitmenttask.cryptospreadranking.config.StaticContent;

import java.io.IOException;

public class EmptySpreadToStringSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(StaticContent.SPREAD_NOT_AVAILABLE);
    }
}
