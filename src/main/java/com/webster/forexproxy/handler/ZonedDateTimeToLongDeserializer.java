package com.webster.forexproxy.handler;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ZonedDateTimeToLongDeserializer extends JsonDeserializer<Long> {

    public static final String ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSz";

    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            return ZonedDateTime.parse(jsonParser.getText(),
                                       DateTimeFormatter.ofPattern(ZONED_DATE_TIME_FORMAT))
                                .toEpochSecond();
        } catch (Exception e) {
            // there were some cases where one frame api returns an invalid timestamp format
            return 0L;
        }
    }
}