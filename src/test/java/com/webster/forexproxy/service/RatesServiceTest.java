package com.webster.forexproxy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.Currency;

@RunWith(MockitoJUnitRunner.class)
public class RatesServiceTest {
    @InjectMocks
    private RatesService ratesService;

    @Test
    public void testGetCurrencyFromJPYToUSD() throws Exception {
        var expected = BigDecimal.ONE;
        var actual = ratesService.getRates(Currency.JPY, Currency.USD);
        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetCurrencyFromUSDToJPY() throws Exception {
        var expected = BigDecimal.ONE;
        var actual = ratesService.getRates(Currency.USD, Currency.JPY);
        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetCurrencyFromUSDToAUD() throws Exception {
        var expected = BigDecimal.ONE;
        var actual = ratesService.getRates(Currency.USD, Currency.AUD);
        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetCurrencyThrowsInvalidRequestException() {
        assertThrows(InvalidRatesRequestException.class,
                     () -> ratesService.getRates(Currency.JPY, Currency.JPY));
    }
}
