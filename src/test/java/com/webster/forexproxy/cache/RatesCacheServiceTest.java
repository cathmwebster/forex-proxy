package com.webster.forexproxy.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
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

        ratesCacheService.refreshCache();
        assertThat(ratesCacheService.get(Currency.NZD)).isNull();
    }

    @Test
    void testCacheGet() {
        ratesCacheService.refreshCache();
        assertThat(ratesCacheService.get(Currency.NZD)).isNull();
        assertThat(ratesCacheService.get(Currency.USD)).isNotNull();
        assertThat(ratesCacheService.get(Currency.USD)).extracting("expiresNanos")
                                                       .isEqualTo(300000000000L);
        assertThat(ratesCacheService.get(Currency.USD)).extracting("price")
                                                       .isEqualTo(new BigDecimal("0.54908647280612891"));
    }
}
