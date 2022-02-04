package com.webster.forexproxy.oneframe.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.webster.forexproxy.handler.ZonedDateTimeDeserializer;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneFrameRateApiResponse {
    @JsonProperty("from")
    private String from;
    @JsonProperty("to")
    private String to;
    @JsonProperty("price")
    private BigDecimal price;

    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    @JsonProperty("time_stamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz")
    private ZonedDateTime timestamp;

    @JsonIgnore
    public long getTimestampInLong() {
        // The timestamp will be in UTC but we want our local timezone
        // convert to asia/tokyo -> epoch second
        // TODO what do we do with timestamp if null ? just return 0?
        return Optional.ofNullable(timestamp)
                .map(t -> LocalDateTime.from(t)
                                  .atZone(ZoneId.of("Asia/Tokyo"))
                                  .toEpochSecond())
                .orElse(0L);
    }
}
