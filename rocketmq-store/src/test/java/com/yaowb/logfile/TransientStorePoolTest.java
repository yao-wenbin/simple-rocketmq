package com.yaowb.logfile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
class TransientStorePoolTest {

    TransientStorePool transientStorePool = new TransientStorePool();

    @Test
    void init() {
        transientStorePool.init();
    }
}
