package com.yaowb.rocketmq.common.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author yaowenbin
 * @Date 2023/5/15
 */
@Slf4j
public class ThreadFactoryImpl implements ThreadFactory {

    private final AtomicLong threadIndex = new AtomicLong(0);
    private final String threadNamePrefix;
    private final boolean daemon = false;

    public ThreadFactoryImpl(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(
                r,
                threadNamePrefix + threadIndex.incrementAndGet()
        );

        thread.setDaemon(daemon);

        thread.setUncaughtExceptionHandler((t, e) ->
            log.error("[BUG] Thread has an uncaught exception, threadId={}, threadName={}",
                    t.getId(), t.getName(), e)
        );

        return thread;
    }

}
