package com.yaowb.rocketmq.namesrv;

import com.yaowb.rocketmq.namesrv.route.BrokerAddrInfo;
import com.yaowb.rocketmq.namesrv.route.RouteInfoManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.channels.Channel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
class RouteInfoManagerTest {

    RouteInfoManager routeInfoManager;

    @Mock
    Channel channel;

    @BeforeEach
    void setUp() {
        routeInfoManager = new RouteInfoManager(
                new NamesrvConfig(), new NamesrvController()
        );
    }


    @Test
    void register() {
        String clusterName = "Default-Cluster-1";
        String brokerName = "Default-Broker-1";
        long brokerId = 0;
        String brokerAddr = "127.0.0.1:9876";

        routeInfoManager.registerBroker(clusterName, brokerAddr, brokerName, brokerId, channel);

        // has brokerData
        assertThat(routeInfoManager.getBrokerAddrTable().get(brokerName)).isNotNull();
        // Cluster contains brokerName
        assertThat(routeInfoManager.getClusterAddrTable().get(clusterName)).contains(brokerName);
        BrokerAddrInfo brokerAddrInfo = new BrokerAddrInfo(clusterName, brokerAddr);
        assertThat(routeInfoManager.getBrokerLiveTable().get(brokerAddrInfo)).isNotNull();
    }

}
