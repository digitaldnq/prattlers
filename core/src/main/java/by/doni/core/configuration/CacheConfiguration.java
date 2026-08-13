package by.doni.core.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@EnableConfigurationProperties(ApplicationCacheProperties.class)
public class CacheConfiguration {

    @Bean
    public CacheManager redisCacheManager(
            ApplicationCacheProperties applicationCacheProperties,
            JedisConnectionFactory connectionFactory
    ) {
        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig();
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        applicationCacheProperties.getCacheNames().forEach(cacheName -> {
            var config = RedisCacheConfiguration.defaultCacheConfig();
            var ttl = applicationCacheProperties.getCaches()
                    .getOrDefault(cacheName, new ApplicationCacheProperties.CacheProperties())
                    .getExpiry();
            if (ttl != null && ttl != Duration.ZERO) {
                config = config.entryTtl(ttl);
            }

            cacheConfigs.put(cacheName, config);
        });

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

}
