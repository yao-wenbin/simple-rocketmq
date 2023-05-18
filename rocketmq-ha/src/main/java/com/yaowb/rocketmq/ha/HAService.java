package com.yaowb.rocketmq.ha;

import com.yaowb.messagestore.DefaultMessageStore;

import java.io.IOException;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 *
 *
 */
public interface HAService {


    void init(DefaultMessageStore defaultMessageStore) throws IOException;

    /**
     * start HA Service
     */
    void start() throws Exception;

    /**
     * shutdown HA Service
     */
    void shutdown() throws Exception;


    void addConnection(HAConnection conn);
}
