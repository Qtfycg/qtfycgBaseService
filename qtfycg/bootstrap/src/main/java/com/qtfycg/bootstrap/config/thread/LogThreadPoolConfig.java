/*
 * Copyright (c)
 * 2026
 * qtfycg
 * All rights reserved
 */

package com.qtfycg.bootstrap.config.thread;


import java.util.concurrent.Executor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
@EnableConfigurationProperties(LogThreadPoolProperties.class)
public class LogThreadPoolConfig {

    @Bean("logExecutor")
    public Executor logExecutor(LogThreadPoolProperties properties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());

        executor.setTaskDecorator(new MdcTaskDecorator());

        executor.setRejectedExecutionHandler(
                new LogRejectedExecutionHandler()
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(
                properties.getAwaitTerminationSeconds()
        );

        executor.initialize();

        return executor;
    }
}
