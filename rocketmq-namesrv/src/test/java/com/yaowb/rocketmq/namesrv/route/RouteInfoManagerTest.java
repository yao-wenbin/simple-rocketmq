package com.yaowb.rocketmq.namesrv.route;

import com.yaowb.rocketmq.namesrv.NamesrvConfig;
import com.yaowb.rocketmq.namesrv.NamesrvController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.channels.Channel;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
class RouteInfoManagerTest {

    RouteInfoManager routeInfoManager;

    static final String clusterName = "Default-Cluster-%d";
    static final String brokerName = "Default-Broker-%d";
    static final long brokerId = 0;
    static final String brokerAddr = "127.0.0.1:987%d";

    @Mock
    Channel channel;


    @BeforeEach
    void setUp() {
        routeInfoManager = new RouteInfoManager(
                new NamesrvConfig(), new NamesrvController()
        );
    }

    void registerDefaultBroker(long brokerNumber) {
        routeInfoManager.registerBroker(
                String.format(clusterName, brokerNumber),
                String.format(brokerAddr, brokerNumber),
                String.format(brokerName, brokerNumber),
                brokerId,
                channel
        );
    }

    @Test
    void format() {
        assertThat(String.format(clusterName, 1)).isEqualTo("Default-Cluster-1");
    }


    @Test
    void registerTopic_shouldRegister_whenBrokerHasRegistered() {
        String topicName = "Default-Topic";
        List<QueueData> topicQueues = Stream.of(new QueueData("Default-Broker"), new QueueData("Default-Broker-2")).toList();

        routeInfoManager.registerBroker("Default-Cluster", "127.0.0.1:9999" ,"Default-Broker", 0, channel);
        routeInfoManager.registerBroker("Default-Cluster", "127.0.0.1:9998" ,"Default-Broker-2", 1, channel);
        routeInfoManager.registerTopic(topicName, topicQueues);

        assertThat(routeInfoManager.getTopicQueueTable().get(topicName)).isNotNull();
        assertThat(routeInfoManager.getTopicQueueTable().get(topicName)).hasSize(2);
    }

    @Test
    void registerTopic_doNothing_whenBrokerNotRegister() {
        String topicName = "Default-Topic";
        List<QueueData> topicQueues = Stream.of(new QueueData("Default-Broker"), new QueueData("Default-Broker-2")).toList();

        routeInfoManager.registerTopic(topicName, topicQueues);

        assertThat(routeInfoManager.getTopicQueueTable().get(topicName)).isNullOrEmpty();
    }

    @Test
    void pickupTopicRouteData() {
        registerDefaultBroker(1);
        registerDefaultBroker(2);
        String topicName = "Default-Topic";
        List<QueueData> topicQueues = Stream.of(new QueueData("Default-Broker-1"), new QueueData("Default-Broker-2")).toList();
        routeInfoManager.registerTopic(topicName, topicQueues);

        TopicRouteData result = routeInfoManager.pickupTopicRouteData("Default-Topic");

        assertThat(result.getBrokerDataList()).isNotEmpty();
        assertThat(result.getQueueDataList()).isNotEmpty();
    }

    @Test
    void pickupTopicRouteData_shouldReturnEmpty_whenTopicNotExists() {
        TopicRouteData result = routeInfoManager.pickupTopicRouteData("Default-Topic");

        assertThat(result.getBrokerDataList()).isEmpty();
        assertThat(result.getQueueDataList()).isEmpty();
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
