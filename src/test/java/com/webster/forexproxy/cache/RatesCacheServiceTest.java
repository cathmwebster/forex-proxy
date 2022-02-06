package com.webster.forexproxy.cache;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.webster.forexproxy.WireMockInitializer;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = { WireMockInitializer.class})
@ActiveProfiles("test")
public class RatesCacheServiceTest {
    @SpyBean
    private RatesCacheService ratesCacheService;

    @Test
    public void testRefreshCacheScheduler() {
        await().atMost(5, TimeUnit.SECONDS)
               .untilAsserted(() -> verify(ratesCacheService, times(5))
                       .refreshCache());
    }
}
