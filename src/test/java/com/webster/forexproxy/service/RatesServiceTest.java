package com.webster.forexproxy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.webster.forexproxy.cache.RatesCacheObject;
import com.webster.forexproxy.cache.RatesCacheService;
import com.webster.forexproxy.exception.DataNotAvailableException;
import com.webster.forexproxy.exception.InvalidRatesRequestException;
import com.webster.forexproxy.model.Currency;
import com.webster.forexproxy.oneframe.client.OneFrameRateApiClient;
import com.webster.forexproxy.oneframe.model.OneFrameRateApiResponse;

@RunWith(MockitoJUnitRunner.class)
public class RatesServiceTest {

    private static final long DEFAULT_CACHE_EXPIRE_NANOS = TimeUnit.SECONDS.toNanos(300);

    @InjectMocks
    private RatesService ratesService;

    @Mock
    private RatesCacheService ratesCacheService;

    @Test
    public void testGetRatesFromJPYToUSD() throws Exception {
        var timestamp = Instant.now().getEpochSecond();
        var expected = new BigDecimal("0.71810472617368925");
        final var mockCache = new RatesCacheObject(expected, timestamp, DEFAULT_CACHE_EXPIRE_NANOS);
        given(ratesCacheService.get(Currency.USD)).willReturn(mockCache);

        var actual = ratesService.getRates(Currency.JPY, Currency.USD);

        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetRatesFromUSDToJPY() throws Exception {
        var timestamp = Instant.now().getEpochSecond();
        final var mockCache = new RatesCacheObject(new BigDecimal("0.7181047261736892"),
                                                   timestamp,
                                                   DEFAULT_CACHE_EXPIRE_NANOS);
        given(ratesCacheService.get(Currency.USD)).willReturn(mockCache);

        var expected = new BigDecimal("1.392554544694820");
        var actual = ratesService.getRates(Currency.USD, Currency.JPY);

        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetRatesFromUSDToAUD() throws Exception {
        var timestamp = Instant.now().getEpochSecond();
        final var mockCacheUSD = new RatesCacheObject(new BigDecimal("0.54908647280612891"),
                                                      timestamp,
                                                      DEFAULT_CACHE_EXPIRE_NANOS);
        given(ratesCacheService.get(Currency.USD)).willReturn(mockCacheUSD);
        final var mockCacheAUD = new RatesCacheObject(new BigDecimal("0.71810472617368925"),
                                                      timestamp,
                                                      DEFAULT_CACHE_EXPIRE_NANOS);
        given(ratesCacheService.get(Currency.AUD)).willReturn(mockCacheAUD);

        var expected = new BigDecimal("1.307817186797164");
        var actual = ratesService.getRates(Currency.USD, Currency.AUD);

        assertThat(actual).extracting("price").isEqualTo(expected);
    }

    @Test
    public void testGetRateThrowsInvalidRequestException() {
        assertThrows(InvalidRatesRequestException.class,
                     () -> ratesService.getRates(Currency.JPY, Currency.JPY));
    }

    @Test
    public void testGetRatesNoCache() {
        assertThrows(DataNotAvailableException.class, () -> ratesService.getRates(Currency.USD, Currency.AUD));
    }
}
