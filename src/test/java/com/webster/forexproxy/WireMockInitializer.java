package com.webster.forexproxy;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.webster.forexproxy.TestUtil.toJson;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import lombok.SneakyThrows;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final ZonedDateTime NOW_UTC = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    @SneakyThrows
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        WireMockServer wireMockServer =
                new WireMockServer(new WireMockConfiguration().dynamicPort()
                                                              .extensions(new ResponseTemplateTransformer(true)));

        wireMockServer.start();

        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });

        // TODO use response template transformer to put dynamic values into mock response
        // was getting not expected values by trying something like {{now}} for time_stamp
        // for now, just manipulate the timestamp to current datetime for all test cases
        // except NZD will have timestamp older than 5 minutes
        final var resMock =
                TestUtil.readJsonAsApiResponse("oneframe/200.json")
                        .stream()
                        .peek(r -> {
                            if ("NZD".equals(r.getTo())) {
                                r.setTimestamp(NOW_UTC.minusMinutes(6));
                            } else if ("GBP".equals(r.getTo())) {
                                r.setTimestamp(NOW_UTC.minusMinutes(1));
                            } else {
                                r.setTimestamp(NOW_UTC);
                            }
                        })
                        .collect(Collectors.toList());

        wireMockServer.stubFor(WireMock.get(urlPathMatching("/rates"))
                                       .withHeader("token", equalTo("testtoken"))
                                       .willReturn(aResponse()
                                                           .withStatus(200)
                                                           .withHeader("content-type", "application/json")
                                                           .withBody(toJson(resMock))));

        TestPropertyValues
                .of(Map.of("one-frame.base-uri", "http://localhost:" + wireMockServer.port() + "/rates"))
                .applyTo(applicationContext);

        applicationContext.getBeanFactory()
                          .registerSingleton("wireMockServer", wireMockServer);
    }
}

