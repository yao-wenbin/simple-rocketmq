package com.yaowb.rocketmq.ha;

import com.yaowb.messagestore.DefaultMessageStore;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
public class DefaultHAClient implements HAClient{

    private final DefaultMessageStore defaultMessageStore;

    public DefaultHAClient(DefaultMessageStore defaultMessageStore) {
        this.defaultMessageStore = defaultMessageStore;
    }

    public void start() {
    }

}
