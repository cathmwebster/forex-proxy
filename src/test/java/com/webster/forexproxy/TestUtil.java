package com.webster.forexproxy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.webster.forexproxy.oneframe.model.OneFrameRateApiResponse;

public class TestUtil {

    public static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String readJson(String path) throws IOException {
        return new String(new ClassPathResource(path)
                                  .getInputStream()
                                  .readAllBytes(), StandardCharsets.UTF_8);
    }

    public static List<OneFrameRateApiResponse> readJsonAsApiResponse(String path) throws IOException {
        return Arrays.asList(objectMapper.readValue(new String(new ClassPathResource(path)
                                  .getInputStream()
                                  .readAllBytes(), StandardCharsets.UTF_8), OneFrameRateApiResponse[].class));
    }

    public static String toJson(List<OneFrameRateApiResponse> responses) throws JsonProcessingException {
        return objectMapper.writeValueAsString(responses);
    }
}
