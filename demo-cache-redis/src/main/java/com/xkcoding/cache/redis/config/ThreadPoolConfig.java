package com.xkcoding.cache.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolConfig {
    public final static int  nThreads = 100000;

    @Bean
    public ThreadPoolExecutor redisPoll() {
        return new ThreadPoolExecutor(
            16,
            64,
            60,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(nThreads));
    }
}
