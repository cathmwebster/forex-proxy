package com.webster.forexproxy.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.model.Rate;
import com.webster.forexproxy.service.RatesService;

@RunWith(SpringRunner.class)
@WebMvcTest(RatesApiController.class)
public class RatesApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatesService ratesService;

    @Test
    public void getRatesJpyToUsdReturn200() throws Exception {
        given(ratesService.getRates(eq(Currency.JPY), eq(Currency.USD)))
                .willReturn(Rate.of(new BigDecimal("0.71810472617368925")));
        mockMvc.perform(get("/v1/rates")
                                .param("from", "JPY")
                                .param("to", "USD"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/jpyToUsd200.json")));
    }

    @Test
    public void getRatesUsdToJpyReturn200() throws Exception {
        given(ratesService.getRates(eq(Currency.USD), eq(Currency.JPY)))
                .willReturn(Rate.of(new BigDecimal(0.71810472617368925)));
        mockMvc.perform(get("/v1/rates")
                                .param("from", "USD")
                                .param("to", "JPY"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/usdToJpy200.json")));
    }

    @Test
    public void getRatesSgdToAudReturn200() throws Exception {
        given(ratesService.getRates(eq(Currency.SGD), eq(Currency.AUD)))
                .willReturn(Rate.of(new BigDecimal(0.415700693982798025)));
        mockMvc.perform(get("/v1/rates")
                                .param("from", "SGD")
                                .param("to", "AUD"))
               .andExpect(status().isOk())
               .andExpect(content().json(readJson("mock/sgdToAud200.json")));
    }

    @Test
    public void getRatesReturn400UnsupportedCurrency() throws Exception {
        mockMvc.perform(get("/v1/rates")
                                .param("from", "KRW")
                                .param("to", "AUD"))
               .andExpect(status().isBadRequest())
               .andExpect(content().json(readJson("mock/unsupportedCurrency400.json")));
    }

    @Test
    public void getRatesReturn400InvalidRatesRequest() throws Exception {
        given(ratesService.getRates(Currency.USD, Currency.USD)).willThrow(new InvalidRatesRequestException());
        mockMvc.perform(get("/v1/rates")
                                .param("from", "USD")
                                .param("to", "USD"))
               .andExpect(status().isBadRequest())
               .andExpect(content().json(readJson("mock/invalidRatesRequest400.json")));
    }

    private String readJson(String path) throws IOException {
        return new String(new ClassPathResource(path)
                                  .getInputStream()
                                  .readAllBytes(), StandardCharsets.UTF_8);
    }
}
