package com.yaowb.rocketmq.common.concurrent;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author yaowenbin
 * @Date 2023/5/15
 */
@Slf4j
@Data
public abstract class ServiceThread implements Runnable {

    protected volatile boolean isDaemon = false;

    protected volatile boolean stopped = false;

    protected Thread thread;


    public abstract String getServiceName();

    public void start() {
        log.info("start thread: {}", getServiceName());
        this.thread = new Thread(this, getServiceName());
        this.thread.setDaemon(isDaemon);
        this.thread.start();
    }

    public void stop() {
        log.info("try to shutdown thread: [{}]", getServiceName());
        this.stopped = true;
    }


    protected boolean isStopped() {
        return stopped;
    }
}
