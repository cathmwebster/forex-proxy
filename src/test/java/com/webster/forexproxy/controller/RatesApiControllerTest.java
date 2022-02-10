package com.webster.forexproxy.controller;

import static com.webster.forexproxy.TestUtil.readJson;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.webster.forexproxy.WireMockInitializer;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { WireMockInitializer.class })
public class RatesApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // TODO do not execute test until cache has been "refreshed" for the first time after application is booted
    // use "await" to wait for the cache to be available

    @Test
    void getRatesJpyToUsdReturn200() {
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> mockMvc.perform(get("/v1/rates")
                                                                                        .param("from", "JPY")
                                                                                        .param("to", "USD"))
                                                                       .andExpect(status().isOk())
                                                                       .andExpect(content().json(readJson(
                                                                               "mock/jpyToUsd200.json"))));
    }

    @Test
    void getRatesUsdToJpyReturn200() throws Exception {
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() ->
                                                                  mockMvc.perform(get("/v1/rates")
                                                                                          .param("from", "USD")
                                                                                          .param("to", "JPY"))
                                                                         .andExpect(status().isOk())
                                                                         .andExpect(jsonPath(
                                                                                 "$.result.timestamp").value(
                                                                                 WireMockInitializer.NOW_UTC.toEpochSecond()))
                                                                         .andExpect(content().json(readJson(
                                                                                 "mock/usdToJpy200.json")))
        );
    }

    @Test
    void getRatesSgdToAudReturn200() throws Exception {
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> mockMvc.perform(get("/v1/rates")
                                                                                        .param("from", "SGD")
                                                                                        .param("to", "AUD"))
                                                                       .andExpect(status().isOk())
                                                                       .andExpect(jsonPath(
                                                                               "$.result.timestamp").value(
                                                                               WireMockInitializer.NOW_UTC.toEpochSecond()))
                                                                       .andExpect(content().json(readJson(
                                                                               "mock/sgdToAud200.json"))));
    }

    @Test
    void getRatesReturn400UnsupportedCurrency() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "KRW")
                                .param("to", "AUD"))
               .andExpect(status().isBadRequest())
               .andExpect(content().json(readJson("mock/unsupportedCurrency400.json")));
    }

    @Test
    void getRatesReturn400InvalidRatesRequest() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "USD")
                                .param("to", "USD"))
               .andExpect(status().isBadRequest())
               .andExpect(content().json(readJson("mock/invalidRatesRequest400.json")));
    }

    @Test
    void getRatesReturn500DataNotAvailable() throws Exception {
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> mockMvc.perform(get("/v1/rates")
                                                                                        .param("from", "JPY")
                                                                                        .param("to", "NZD"))
                                                                       .andExpect(
                                                                               status().isInternalServerError())
                                                                       .andExpect(content().json(readJson(
                                                                               "mock/dataNotAvailable500.json"))));
    }
}
