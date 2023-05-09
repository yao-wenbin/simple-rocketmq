package com.yaowb.rocketmq.namesrv.route;

import com.yaowb.MixAll;
import com.yaowb.rocketmq.namesrv.NamesrvConfig;
import com.yaowb.rocketmq.namesrv.NamesrvController;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
@Getter
@Slf4j
public class RouteInfoManager {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    // 默认的Broker心跳超时间
    // Default Broker Heartbeat expired time.
    private final static long DEFAULT_BROKER_CHANNEL_EXPIRED_TIME = 1000 * 60 * 2;

    private final Map<String /* topic */, Map<String, QueueData>> topicQueueTable = new ConcurrentHashMap<>(1024);
    private final Map<String /* brokerName */, BrokerData> brokerAddrTable = new ConcurrentHashMap<>(128);
    private final Map<String /* clusterName */, Set<String/* brokerName */>> clusterAddrTable = new ConcurrentHashMap<>(32);
    private final Map<BrokerAddrInfo /* brokerAddr */, BrokerLiveInfo> brokerLiveTable = new ConcurrentHashMap<>(256);

    private final NamesrvConfig namesrvConfig;
    private final NamesrvController namesrvController;

    public RouteInfoManager(final NamesrvConfig config, final NamesrvController controller) {
        this.namesrvConfig = config;
        this.namesrvController = controller;
    }


    public RegisterBrokerResult registerBroker(String clusterName,
                               String brokerAddr,
                               String brokerName,
                               long brokerId,
                               Channel channel
                               ) {
        RegisterBrokerResult result = new RegisterBrokerResult();
        try {
            lock.writeLock().lockInterruptibly();
            // 1. deal with clusterAddrTable.
            // 处理clusterAddrTable

            // init cluster's addr if absent.
            // 初始化ClusterAddrTable中对应的ClusterName
            Set<String> brokerNames = this.clusterAddrTable.computeIfAbsent(clusterName, k -> new HashSet<>());
            brokerNames.add(brokerName);

            // 2. deal with brokerAddrTable.
            // 处理brokerAddrTable
            boolean firstRegister = false;
            BrokerData brokerData = this.brokerAddrTable.get(brokerName);
            if (brokerData == null) {
                firstRegister = true;
                brokerData = new BrokerData(clusterName, brokerName, new HashMap<>());
                this.brokerAddrTable.put(brokerName, brokerData);
            }


            // 3. deal with topicQueueTable.
            // 3. 处理topicQueueTable.
            // TODO


            // 4. deal with brokerLiveInfoTable.
            // 4. 处理brokerLiveInfoTable.
            BrokerAddrInfo brokerAddrInfo = new BrokerAddrInfo(clusterName, brokerAddr);
            BrokerLiveInfo brokerLiveInfo = new BrokerLiveInfo(System.currentTimeMillis(),
                    DEFAULT_BROKER_CHANNEL_EXPIRED_TIME,
                    new DataVersion(),
                    channel);
            BrokerLiveInfo prevBrokerLiveInfo = brokerLiveTable.put(brokerAddrInfo, brokerLiveInfo);
            if (prevBrokerLiveInfo == null) {
                log.info("new broker registered: {}", brokerAddrInfo);
            }

            // Deal With Slave Server.
            // 处理从节点.
            if (MixAll.MASTER_ID != brokerId) {
                String masterAddr = brokerData.getBrokerAddrs().get(MixAll.MASTER_ID);
                if (masterAddr != null) {
                    BrokerAddrInfo masterKey = new BrokerAddrInfo(clusterName, masterAddr);
                    BrokerLiveInfo masterLiveInfo = this.brokerLiveTable.get(masterKey);
                    if (masterLiveInfo != null) {
                        result.setMasterAddr(masterAddr);
                    }
                }

            }

        } catch (InterruptedException e) {
            log.warn("interrupted exception :", e);
            Thread.currentThread().interrupt();
        } finally {
            lock.writeLock().unlock();
        }

        return result;
    }
}
