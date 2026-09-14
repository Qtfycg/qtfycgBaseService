/*
 * Copyright (c)
 * 2026
 * qtfycg
 * All rights reserved
 */

package com.qtfycg.bootstrap.config.thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "qtfycg.thread-pool.log")
public class LogThreadPoolProperties {

    private int corePoolSize = 2;

    private int maxPoolSize = 4;

    private int queueCapacity = 1000;

    private int keepAliveSeconds = 60;

    private String threadNamePrefix = "qtfycg-log-";

    private int awaitTerminationSeconds = 10;
}
