package com.yaowb.rocketmq.namesrv.route;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @Author yaowenbin
 * @Date 2023/5/10
 */
class BrokerLiveInfoTest {

    BrokerLiveInfo brokerLiveInfo = new BrokerLiveInfo();

    @BeforeEach
    public void setUp() {
        brokerLiveInfo.setHeartbeatTimeoutMills(120 * 1000);
        brokerLiveInfo.setLastUpdateTimestamp(0L);
    }

    @Test
    void expired_shouldReturnTrue_WhenCurrentReduceLastOverTimeout() {
        assertTrue(brokerLiveInfo.expired(System.currentTimeMillis()));
    }

    @Test
    void expired_shouldReturnTrue_WhenCurrentReduceLastUnderTimeout() {
        long now = System.currentTimeMillis();
        brokerLiveInfo.setLastUpdateTimestamp(now - 30 * 10000);

        assertTrue(brokerLiveInfo.expired(now));
    }
}
