/*
 * Copyright (c)
 * 2026
 * qtfycg
 * All rights reserved
 */

package com.qtfycg.bootstrap.config.thread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
        log.warn(
                "日志线程池任务被拒绝，poolSize={}, activeCount={}, queueSize={}",
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getQueue().size()
        );
    }
}
