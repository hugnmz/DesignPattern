package com.hungnguyen.coffee.restapitjava.configuration;

import jdk.jfr.Enabled;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Slf4j
@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String getRedisHost;

    @Value("${String.redis.port}")
    private String redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    Bean
    public RedisTemplate<String, Object> redisTemplate() {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
}
