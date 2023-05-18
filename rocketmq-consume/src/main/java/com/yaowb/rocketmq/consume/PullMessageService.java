package com.yaowb.rocketmq.consume;

import com.yaowb.rocketmq.common.concurrent.ServiceThread;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author yaowenbin
 * @Date 2023/5/15
 */
@Slf4j
public class PullMessageService extends ServiceThread {

    private final LinkedBlockingQueue<MessageResult> messageRequestQueue = new LinkedBlockingQueue<MessageResult>();

    private final ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor();

    @Override
    public String getServiceName() {
        return "PullMessageService";
    }

    @Override
    public void run() {
        while (!isStopped()) {
            try {
                MessageResult req = messageRequestQueue.take();
                if (req != null) {

                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
