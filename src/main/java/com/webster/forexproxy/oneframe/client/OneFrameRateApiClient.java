package com.webster.forexproxy.oneframe.client;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.webster.forexproxy.exception.OneFrameApiException;
import com.webster.forexproxy.handler.JsonBodyHandler;
import com.webster.forexproxy.oneframe.config.OneFrameConfiguration;
import com.webster.forexproxy.oneframe.model.OneFrameRateApiResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OneFrameRateApiClient {

    private final OneFrameConfiguration oneFrameConfiguration;

    public OneFrameRateApiClient(OneFrameConfiguration oneFrameConfiguration) {
        this.oneFrameConfiguration = oneFrameConfiguration;
    }

    /**
     * Calls One Frame Rate API to fetch currency rates for the given pairs of currencies
     * @param pairs of currency strings "AB"
     * @return api response
     */
    public List<OneFrameRateApiResponse> getRates(List<String> pairs) throws OneFrameApiException {
        final var client = HttpClient.newHttpClient();
        var uriComponentsBuilder = UriComponentsBuilder.fromUriString(oneFrameConfiguration.getBaseUri());
        pairs.forEach(p -> uriComponentsBuilder.queryParam("pair", p));
        var request = HttpRequest.newBuilder(uriComponentsBuilder.build().toUri())
                                 .header("token", oneFrameConfiguration.getToken())
                                 .build();
        try {
            var response = client.send(request, new JsonBodyHandler<>(OneFrameRateApiResponse[].class));
            if (response.statusCode() > 200) {
                throw new OneFrameApiException("One Frame Rate API returned error status code " + response.statusCode());
            }
            if (response.body() == null) {
                throw new OneFrameApiException("Unable to parse One Frame Rate API response body");
            }
            return Arrays.asList(response.body());
        } catch (IOException | InterruptedException e) {
            log.error("Unxpected error occured when calling One Frame Rate API", e);
            throw new OneFrameApiException("Unxpected error occured when calling One Frame Rate API");
        }
    }
}
