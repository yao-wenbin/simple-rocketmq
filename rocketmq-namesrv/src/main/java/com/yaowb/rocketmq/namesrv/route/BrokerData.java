package com.yaowb.rocketmq.namesrv.route;

import lombok.Data;

import java.util.Map;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
@Data
public class BrokerData {

    private String clusterName;
    private String brokerName;
    private Map<Long/* brokerId*/, String/* brokerAddr */> brokerAddrs;

    public BrokerData(String clusterName, String brokerName, Map<Long, String> brokerAddrs) {
        this.clusterName = clusterName;
        this.brokerName = brokerName;
        this.brokerAddrs = brokerAddrs;
    }
}

