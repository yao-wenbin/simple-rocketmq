package com.yaowb.rocketmq.namesrv.route;

import com.yaowb.rocketmq.common.utils.MixAll;
import com.yaowb.rocketmq.namesrv.NamesrvConfig;
import com.yaowb.rocketmq.namesrv.NamesrvController;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    private final Map<String /* topic */, Map<String/* brokerName */, QueueData>> topicQueueTable = new ConcurrentHashMap<>(1024);
    private final Map<String /* brokerName */, BrokerData> brokerAddrTable = new ConcurrentHashMap<>(128);
    private final Map<String /* clusterName */, Set<String/* brokerName */>> clusterAddrTable = new ConcurrentHashMap<>(32);
    private final Map<BrokerAddrInfo /* brokerAddr */, BrokerLiveInfo> brokerLiveTable = new ConcurrentHashMap<>(256);

    private final NamesrvConfig namesrvConfig;
    private final NamesrvController namesrvController;

    public RouteInfoManager(final NamesrvConfig config, final NamesrvController controller) {
        this.namesrvConfig = config;
        this.namesrvController = controller;
    }


    /**
     * Router Manager will process the register request
     * and then register broker data to related tables.
     */
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

    // public void scanNoActivateBroker() {
    //     long nowTimeMills = System.currentTimeMillis();
    //     brokerLiveTable.forEach(((addrInfo, liveInfo) -> {
    //         if (liveInfo.expired(nowTimeMills)) {
    //             unRegisterBroker(addrInfo);
    //         }
    //     }));
    // }

    /**
     * to remove broker or reduce the broker instance count from tables.
     */
    public void unRegisterBroker(Set<UnRegisterBroekrRequest> unRegisterBrokerRequest) {
        Set<String> removedBroker = new HashSet<>();

        try {
            this.lock.writeLock().lockInterruptibly();

            for (final UnRegisterBroekrRequest request : unRegisterBrokerRequest) {
                final String brokerName = request.getBrokerName();
                final String clusterName = request.getClusterName();
                final String brokerAddr = request.getBrokerAddr();

                brokerLiveTableRemove(clusterName, brokerAddr);

                boolean nameRemoved = brokerAddrTableRemove(brokerName, brokerAddr);

                if (nameRemoved) {
                    clusterAddrTableRemove(clusterName, brokerName);
                    removedBroker.add(brokerName);
                }
            }

            if (!removedBroker.isEmpty()) {
                cleanTopicByUnRegisterRequests(removedBroker);
            }

        } catch (InterruptedException e) {
            log.warn("interruptedException",e);
            Thread.currentThread().interrupt();
        }


    }

    private void clusterAddrTableRemove(String clusterName, String brokerName) {
        Set<String> brokerNameSet = this.clusterAddrTable.get(clusterName);
        if (brokerNameSet != null) {
            boolean remove = brokerNameSet.remove(brokerName);
            log.info("unRegister Broker, remove name: {} from clusterAddrTable {}", brokerName,remove);
        }

        if (brokerNameSet == null || brokerNameSet.isEmpty()) {
            this.clusterAddrTable.remove(clusterName);
            log.info("unRegister Broker, remove cluster: {} from clusterName", clusterName);
        }
    }

    private boolean brokerAddrTableRemove(String brokerName, String brokerAddr) {
        boolean nameRemoved = false;
        BrokerData brokerData = brokerAddrTable.get(brokerName);
        if (brokerData != null) {
            boolean removed = brokerData.getBrokerAddrs().entrySet().removeIf(addr -> brokerAddr.equals(addr.getValue()));

            log.info("unRegisterBroker, remove addr from brokerAddrTable {}", removed);

            if (brokerData.getBrokerAddrs().isEmpty()) {
                this.brokerAddrTable.remove(brokerName);
                log.info("unReigsterBroker, remove name: {} from brokerAddrTable true", brokerName);
                nameRemoved = true;
            }
        }
        return nameRemoved;
    }

    private void brokerLiveTableRemove(String clusterName, String brokerAddr) {
        BrokerAddrInfo addrInfo = new BrokerAddrInfo(clusterName, brokerAddr);
        BrokerLiveInfo liveInfo = brokerLiveTable.remove(addrInfo);
        log.info("unRegisterBroker, remove {}, from brokerLiveTable {}", addrInfo, liveInfo == null ? "Failed" : "OK");
    }

    private void cleanTopicByUnRegisterRequests(Set<String> removedBroker) {
        Iterator<Map.Entry<String, Map<String, QueueData>>> itMap
                = this.topicQueueTable.entrySet().iterator();

        while(itMap.hasNext()) {
            Map.Entry<String, Map<String, QueueData>> entry = itMap.next();

            String topic = entry.getKey();
            Map<String, QueueData> queueDataMap = entry.getValue();

            for (final String brokerName : removedBroker) {
                QueueData removedQueueData = queueDataMap.remove(brokerName);
                if (removedQueueData != null) {
                    log.info("removeTopicByBrokerName, remove topic [{}] one broker's queue [{}]", topic, removedQueueData);
                }
                if (queueDataMap.isEmpty()) {
                    itMap.remove();
                    log.info("removeTopicByBrokerName, remove the topic [{}] all queue", topic);
                }

            }
        }


    }

    public void registerTopic(String topicName, List<QueueData> topicQueues) {
        if (topicQueues.isEmpty()) {
            return;
        }

        try {
            this.lock.writeLock().lockInterruptibly();
            if (this.topicQueueTable.get(topicName) != null) {
                log.error("Register has been created");
                return;
            }

            Map<String, QueueData> queueDataMap = new HashMap<>(topicQueues.size());
            for (QueueData data : topicQueues) {
                if (this.brokerAddrTable.get(data.getBrokerName()) == null) {
                    log.warn("Register topic data has illegal broker: {}", data.getBrokerName());
                    return;
                }
                queueDataMap.put(data.getBrokerName(), data);
            }

            this.topicQueueTable.put(topicName, queueDataMap);
            log.info("Register topic {}, route {}", topicName, topicQueues);

        } catch (InterruptedException e) {
            log.warn("Interrupted exception", e);
            Thread.currentThread().interrupt();
        }

    }

    public TopicRouteData pickupTopicRouteData(String topic) {
        TopicRouteData result = new TopicRouteData();
        try {
            this.lock.readLock().lockInterruptibly();

            Map<String, QueueData> queueDataMap = this.topicQueueTable.get(topic);
            if (queueDataMap == null) {
                return result;
            }

            result.setBrokerDataList(queueDataMap.values());
            result.setQueueDataList(queueDataMap.keySet());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return result;
    }
}
