package com.webster.forexproxy;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import lombok.SneakyThrows;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SneakyThrows
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        WireMockServer wireMockServer =
                new WireMockServer(new WireMockConfiguration().dynamicPort());

        wireMockServer.start();

        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });

        TestPropertyValues
                .of(Map.of("one-frame.base-uri", "http://localhost:" + wireMockServer.port() + "/rates"))
                .applyTo(applicationContext);

        applicationContext.getBeanFactory()
                          .registerSingleton("wireMockServer", wireMockServer);
    }
}
