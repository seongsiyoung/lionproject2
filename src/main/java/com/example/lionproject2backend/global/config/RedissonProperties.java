package com.example.lionproject2backend.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {

    private String address;
    private String username;
    private String password;
    private int database = 0;
    private int timeout = 3000;
    private int connectTimeout = 10000;
    private int idleConnectionTimeout = 10000;
    private int retryAttempts = 3;
    private int retryInterval = 1500;
    private int connectionPoolSize = 24;
    private int connectionMinimumIdleSize = 8;
    private int threads = 16;
    private int nettyThreads = 32;
}
