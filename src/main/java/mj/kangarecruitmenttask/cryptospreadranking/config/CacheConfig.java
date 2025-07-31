package mj.kangarecruitmenttask.cryptospreadranking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.available-pairs-ttl}")
    private long availablePairsTtl;

    @Value("${cache.orderbook-ttl}")
    private long orderbookTtl;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                "availablePairs", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(availablePairsTtl)),
                "orderBook", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(orderbookTtl))
        );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
