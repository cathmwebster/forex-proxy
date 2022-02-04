package com.webster.forexproxy.oneframe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("one-frame")
@Data
public class OneFrameConfiguration {
    private String baseUri;
    private String token;
}
