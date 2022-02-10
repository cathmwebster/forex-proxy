package com.webster.forexproxy.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webster.forexproxy.WireMockInitializer;
import com.webster.forexproxy.model.Currency;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = { WireMockInitializer.class })
public class RatesCacheServiceTest {

    @SpyBean
    private RatesCacheService ratesCacheService;

    @Test
    void testRefreshCacheScheduler() {
        await().atMost(5, TimeUnit.SECONDS)
               .untilAsserted(() -> verify(ratesCacheService, times(5))
                       .refreshCache());
    }

    @Test
    void testCacheGet() {
        ratesCacheService.refreshCache();
        var nzdRate = ratesCacheService.get(Currency.NZD);
        assertThat(nzdRate).isNull();

        var usdRate = ratesCacheService.get(Currency.USD);
        assertThat(usdRate).isNotNull();
        assertThat(usdRate).extracting("price")
                           .isEqualTo(new BigDecimal("0.54908647280612891"));
        assertThat(usdRate).extracting("timestamp")
                           .isEqualTo(WireMockInitializer.NOW_UTC);
        // I want to assert that the expire nanos is no more than 5 minutes
        assertTrue(usdRate.getExpiresNanos() <= TimeUnit.SECONDS.toNanos(300));

        var gbpRate = ratesCacheService.get(Currency.GBP);
        assertThat(usdRate).isNotNull();
        assertThat(gbpRate).extracting("timestamp")
                           .isEqualTo(WireMockInitializer.NOW_UTC.minusMinutes(1));
        // I want to assert that the expire nanos is no more than 4 minutes
        assertTrue(gbpRate.getExpiresNanos() <= TimeUnit.SECONDS.toNanos(240));
    }
}
