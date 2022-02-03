package com.webster.forexproxy.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(RatesApiController.class)
public class RatesApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getRatesJpyToUsdReturn200() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "JPY")
                                .param("to", "USD"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/jpyToUsd200.json")));
    }

    @Test
    public void getRatesUsdToJpyReturn200() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "USD")
                                .param("to", "JPY"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/usdToJpy200.json")));
    }

    @Test
    public void getRatesSgdToAudReturn200() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "SGD")
                                .param("to", "AUD"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/sgdToAud200.json")));
    }

    private String readJson(String path) throws IOException {
        return new String(new ClassPathResource(path)
                                  .getInputStream()
                                  .readAllBytes(), StandardCharsets.UTF_8);
    }
}
