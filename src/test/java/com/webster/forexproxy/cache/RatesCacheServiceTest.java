package com.webster.forexproxy.cache;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.webster.forexproxy.WireMockInitializer;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = { WireMockInitializer.class})
@ActiveProfiles("test")
public class RatesCacheServiceTest {
    @SpyBean
    private RatesCacheService ratesCacheService;

    @Test
    void testRefreshCacheScheduler() {
        await().atMost(5, TimeUnit.SECONDS)
               .untilAsserted(() -> verify(ratesCacheService, times(5))
                       .refreshCache());
    }
}
