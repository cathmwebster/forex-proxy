package com.webster.forexproxy;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import lombok.SneakyThrows;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SneakyThrows
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        // TODO use response template transformer to put dynamic values into mock response
        // was getting not expected values by trying something like {{now}} for time_stamp
        WireMockServer wireMockServer =
                new WireMockServer(new WireMockConfiguration().dynamicPort()
                                                              .extensions(new ResponseTemplateTransformer(true)));

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
