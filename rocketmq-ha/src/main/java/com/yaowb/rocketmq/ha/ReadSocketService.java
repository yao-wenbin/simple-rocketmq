package com.yaowb.rocketmq.ha;

import com.yaowb.rocketmq.common.concurrent.ServiceThread;

import java.nio.channels.SocketChannel;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
public class ReadSocketService extends ServiceThread {

    public ReadSocketService(SocketChannel sc) {

    }


    @Override
    public void run() {

    }

    @Override
    public String getServiceName() {
        return "Read Socket Service";
    }
}
