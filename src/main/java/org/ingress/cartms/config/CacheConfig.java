package org.ingress.cartms.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    @Value("${redis.server.urls}")
    private String redisHost;

    @Bean
    public RedissonClient redissonClient() {
        var config = new Config();
        config
                .setCodec(new SerializationCodec())
                .useSingleServer()
                .setAddress(redisHost);
        return Redisson.create(config);
    }
}
