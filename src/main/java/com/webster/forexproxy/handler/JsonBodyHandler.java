package com.webster.forexproxy.handler;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json handler class for HttpClient response
 *
 * @param <T>
 */
public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {

    private final Class<T> clazz;
    private final ObjectMapper objectMapper;

    public JsonBodyHandler(Class<T> clazz) {
        this.clazz = clazz;
        objectMapper = new ObjectMapper()
                .configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
        return asJSON(clazz);
    }

    public <T> HttpResponse.BodySubscriber<T> asJSON(Class<T> targetType) {
        HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(
                upstream,
                (String body) -> {
                    try {
                        return objectMapper.readValue(body, targetType);
                    } catch (IOException e) {
                        return null;
                    }
                });
    }
}
