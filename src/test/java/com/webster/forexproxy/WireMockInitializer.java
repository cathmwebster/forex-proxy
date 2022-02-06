package com.webster.forexproxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import lombok.SneakyThrows;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SneakyThrows
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        WireMockServer wireMockServer =
                new WireMockServer(new WireMockConfiguration().port(9000));

        wireMockServer.start();

        applicationContext.addApplicationListener(applicationEvent -> {
            if (applicationEvent instanceof ContextClosedEvent) {
                wireMockServer.stop();
            }
        });

        applicationContext.getBeanFactory()
                          .registerSingleton("wireMockServer", wireMockServer);
    }
}
