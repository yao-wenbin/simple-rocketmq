package com.yaowb.rocketmq.ha;

import com.yaowb.rocketmq.common.concurrent.ServiceThread;

import java.nio.channels.SocketChannel;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
public class WriteSocketService extends ServiceThread {
    public WriteSocketService(SocketChannel sc) {

    }

    @Override
    public String getServiceName() {
        return "Write Socket Serivce";
    }

    @Override
    public void run() {

    }
}
