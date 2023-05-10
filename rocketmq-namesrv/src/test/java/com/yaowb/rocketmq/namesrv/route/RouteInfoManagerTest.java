package com.yaowb.rocketmq.namesrv.route;

import com.yaowb.rocketmq.namesrv.NamesrvConfig;
import com.yaowb.rocketmq.namesrv.NamesrvController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.channels.Channel;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test
    void unRegister() {
        register();
        String clusterName = "Default-Cluster-1";
        String brokerName = "Default-Broker-1";
        String brokerAddr = "127.0.0.1:9876";

        UnRegisterBroekrRequest request = new UnRegisterBroekrRequest(brokerName, brokerAddr, clusterName);
        routeInfoManager.unRegisterBroker(Stream.of(request).collect(Collectors.toSet()));

        BrokerAddrInfo addrInfo = new BrokerAddrInfo(clusterName, brokerAddr);
        assertThat(routeInfoManager.getBrokerLiveTable().get(addrInfo)).isNull();
        assertThat(routeInfoManager.getBrokerAddrTable().get(brokerName)).isNull();
        assertThat(routeInfoManager.getClusterAddrTable().get(clusterName)).isNull();
    }

}
