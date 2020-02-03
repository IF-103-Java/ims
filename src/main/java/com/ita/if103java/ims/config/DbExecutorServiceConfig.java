package com.ita.if103java.ims.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class DbExecutorServiceConfig {

    @Value("${db.executor.fixedThreadPool.size}")
    private int fixedThreadPoolSize;

    @Bean("dbFixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(fixedThreadPoolSize);
    }

}
