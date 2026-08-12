package com.example.lionproject2backend.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.springframework.util.StringUtils.hasText;

@Configuration
@EnableRedisRepositories
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean sslEnabled;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        if (hasText(password)) {
            configuration.setPassword(RedisPassword.of(password));
        }

        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigurationBuilder =
                LettuceClientConfiguration.builder();
        if (sslEnabled) {
            clientConfigurationBuilder.useSsl();
        }

        return new LettuceConnectionFactory(configuration, clientConfigurationBuilder.build());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key: StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Value: Jackson2JsonRedisSerializer
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        return redisTemplate;
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedissonProperties redissonProperties) {
        Config config = new Config();
        config.setThreads(redissonProperties.getThreads());
        config.setNettyThreads(redissonProperties.getNettyThreads());

        var singleServerConfig = config.useSingleServer()
                .setAddress(redissonProperties.getAddress())
                .setDatabase(redissonProperties.getDatabase())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectTimeout(redissonProperties.getConnectTimeout())
                .setIdleConnectionTimeout(redissonProperties.getIdleConnectionTimeout())
                .setRetryAttempts(redissonProperties.getRetryAttempts())
                .setRetryInterval(redissonProperties.getRetryInterval())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize());

        if (hasText(redissonProperties.getUsername())) {
            singleServerConfig.setUsername(redissonProperties.getUsername());
        }

        if (hasText(redissonProperties.getPassword())) {
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }

        return Redisson.create(config);
    }
}
