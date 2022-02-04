package com.webster.forexproxy.oneframe.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.webster.forexproxy.handler.ZonedDateTimeToLongDeserializer;

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

    @JsonDeserialize(using = ZonedDateTimeToLongDeserializer.class)
    @JsonProperty("time_stamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz")
    private Long timestamp;
}
