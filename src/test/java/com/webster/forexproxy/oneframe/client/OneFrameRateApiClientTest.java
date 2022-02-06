package com.webster.forexproxy.oneframe.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.webster.forexproxy.exception.OneFrameApiException;
import com.webster.forexproxy.oneframe.config.OneFrameConfiguration;

public class OneFrameRateApiClientTest {

    private static final List<String> REQUEST_ALL_CURRENCIES =
            List.of("JPYAUD", "JPYCAD", "JPYCHF", "JPYEUR", "JPYGBP", "JPYNZD", "JPYSGD", "JPYUSD");

    public WireMockServer wireMockServer;

    private OneFrameRateApiClient oneFrameRateApiClient;

    @BeforeEach
    public void init() {
        wireMockServer = new WireMockRule(options().dynamicPort());
        wireMockServer.start();
        final OneFrameConfiguration config = new OneFrameConfiguration();
        config.setToken("testtoken");
        config.setBaseUri(String.format("http://localhost:%d/rates", wireMockServer.port()));
        oneFrameRateApiClient = new OneFrameRateApiClient(config);
    }

    @AfterEach
    public void shutdown() {
        wireMockServer.shutdown();
    }

    @Test
    void testGet200() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/rates"))
                                     .withHeader("token", equalTo("testtoken"))
                                     .willReturn(aResponse()
                                                         .withStatus(200)
                                                         .withHeader("content-type", "application/json")
                                                         .withBody(readJson("oneframe/200.json"))));
        final var actual = oneFrameRateApiClient.getRates(REQUEST_ALL_CURRENCIES);
        assertThat(actual).isNotEmpty();
        assertThat(actual.size()).isEqualTo(8);
    }

    @Test
    void testGet500ThrowException() {
        wireMockServer.stubFor(get(urlPathMatching("/rates"))
                                     .withHeader("token", equalTo("testtoken"))
                                     .willReturn(aResponse()
                                                         .withStatus(500)));
        assertThrows(OneFrameApiException.class, () -> oneFrameRateApiClient.getRates(REQUEST_ALL_CURRENCIES));
    }

    @Test
    void testGetInvalidTimestampReturnResponse() throws Exception {
        wireMockServer.stubFor(get(urlPathMatching("/rates"))
                                     .withHeader("token", equalTo("testtoken"))
                                     .willReturn(aResponse()
                                                         .withStatus(200)
                                                         .withHeader("content-type", "application/json")
                                                         .withBody(readJson("oneframe/invalidFormatRes.json"))));
        final var actual = oneFrameRateApiClient.getRates(REQUEST_ALL_CURRENCIES);
        assertThat(actual).hasSize(8);
        assertThat(actual).extracting("timestamp").containsOnlyOnce(0L);
    }

    @Test
    void testConnectionResetByPeerThrowsException() {
        wireMockServer.stubFor(get(urlPathMatching("/rates"))
                                     .withHeader("token", equalTo("testtoken"))
                                     .willReturn(aResponse()
                                                         .withStatus(200)
                                                         .withFault(Fault.CONNECTION_RESET_BY_PEER)));
        assertThrows(OneFrameApiException.class, () -> oneFrameRateApiClient.getRates(REQUEST_ALL_CURRENCIES));
    }

    private String readJson(String path) throws IOException {
        return new String(new ClassPathResource(path)
                                  .getInputStream()
                                  .readAllBytes(), StandardCharsets.UTF_8);
    }
}
