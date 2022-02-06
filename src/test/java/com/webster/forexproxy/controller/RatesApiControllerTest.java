package com.webster.forexproxy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.webster.forexproxy.WireMockInitializer;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = { WireMockInitializer.class})
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class RatesApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getRatesJpyToUsdReturn200() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "JPY")
                                .param("to", "USD"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/jpyToUsd200.json"), true));
    }

    @Test
    void getRatesUsdToJpyReturn200() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "USD")
                                .param("to", "JPY"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/usdToJpy200.json"), true));
    }

    @Test
    void getRatesSgdToAudReturn200() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "SGD")
                                .param("to", "AUD"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/sgdToAud200.json"), true));
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

//    @Test
//    void getRatesReturn500DataNotAvailable() throws Exception {
//        mockMvc.perform(get("/v1/rates")
//                                .param("from", "AUD")
//                                .param("to", "USD"))
//               .andExpect(status().isInternalServerError())
//               .andExpect(content().json(readJson("mock/dataNotAvailable500.json")));
//    }

    private String readJson(String path) throws IOException {
        return new String(new ClassPathResource(path)
                                  .getInputStream()
                                  .readAllBytes(), StandardCharsets.UTF_8);
    }
}
