package com.yaowb.rocketmq.common.concurrent;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @Author yaowenbin
 * @Date 2023/5/15
 */
@Slf4j
@Data
public abstract class ServiceThread implements Runnable {

    // 关闭线程时，加入主线程的最大等待时间
    private static final long JOIN_TIME = 90 * 1000;

    protected volatile boolean isDaemon = false;

    // 当前线程是否并创建并且启动
    protected volatile boolean stopped = false;

    protected volatile AtomicBoolean hasNotified = new AtomicBoolean(false);

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


    public void shutdown(boolean interrupt) {
        if (isStopped()) {
            return;
        }
        // 通知线程状态已经停止，在run方法中的while语句条件的getStopped将会返回false
        this.stopped = true;
        log.info("shutdown thread[{}], interrupt={}", getServiceName(), interrupt);
        try {
            // 如果是中断参数为true，那么手动调用interrupted方法
            if(interrupt) {
                this.thread.interrupt();
            }
            long beginTime = System.currentTimeMillis();
            // 等待线程真正执行完毕
            if (!this.isDaemon) {
                this.thread.join(getJoinTime());
            }
            long elapsedTime = System.currentTimeMillis() - beginTime;
            log.info("join thread: [{}], elapsedTime: {}, join Time: {}", getServiceName(), elapsedTime, getJoinTime());
        } catch (InterruptedException e) {
            log.error("shutdown interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    protected void waitForRunning(long interval) {
       synchronized (this) {
           try {
               wait(interval);
           } catch (InterruptedException e) {
               log.error("wait for running interrupted exception",e);
               Thread.currentThread().interrupt();
           }
       }
    }

    protected boolean isStopped() {
        return stopped;
    }

    protected long getJoinTime() {
        return JOIN_TIME;
    }
}
