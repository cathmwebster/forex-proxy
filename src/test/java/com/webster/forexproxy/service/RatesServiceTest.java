package com.webster.forexproxy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.oneframe.client.OneFrameRateApiClient;
import com.webster.forexproxy.oneframe.model.OneFrameRateApiResponse;

@RunWith(MockitoJUnitRunner.class)
public class RatesServiceTest {
    @InjectMocks
    private RatesService ratesService;

    @Mock
    private OneFrameRateApiClient oneFrameRateApiClient;

    @Test
    public void testGetCurrencyFromJPYToUSD() throws Exception {
        var expected = new BigDecimal("0.71810472617368925");
        final var mockRes = new OneFrameRateApiResponse();
        mockRes.setPrice(expected);
        given(oneFrameRateApiClient.getRates(List.of("JPYUSD"))).willReturn(List.of(mockRes));

        var actual = ratesService.getRates(Currency.JPY, Currency.USD);

        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetCurrencyFromUSDToJPY() throws Exception {
        var expected = new BigDecimal("0.71810472617368925");
        final var mockRes = new OneFrameRateApiResponse();
        mockRes.setPrice(expected);
        given(oneFrameRateApiClient.getRates(List.of("USDJPY"))).willReturn(List.of(mockRes));

        var actual = ratesService.getRates(Currency.USD, Currency.JPY);

        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetCurrencyFromUSDToAUD() throws Exception {
        var expected = new BigDecimal("0.415700693982798025");
        final var mockRes = new OneFrameRateApiResponse();
        mockRes.setPrice(expected);
        given(oneFrameRateApiClient.getRates(List.of("USDAUD"))).willReturn(List.of(mockRes));

        var actual = ratesService.getRates(Currency.USD, Currency.AUD);

        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetCurrencyThrowsInvalidRequestException() {
        assertThrows(InvalidRatesRequestException.class,
                     () -> ratesService.getRates(Currency.JPY, Currency.JPY));
    }
}
