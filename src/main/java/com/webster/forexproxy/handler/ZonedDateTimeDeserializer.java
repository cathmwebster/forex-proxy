package com.webster.forexproxy.handler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    public static final String ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSz";

    @Override
    public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        try {
            return ZonedDateTime.parse(jsonParser.getText(),
                                       DateTimeFormatter.ofPattern(ZONED_DATE_TIME_FORMAT));
        } catch (Exception e) {
            // there were some cases where one frame api returns an invalid timestamp format
            return null;
        }
    }
}