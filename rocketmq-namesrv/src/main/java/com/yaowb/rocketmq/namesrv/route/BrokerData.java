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
    private String brokername;
    private Map<Long, String> brokerAddrs;

    public BrokerData(String clusterName, String brokername, Map<Long, String> brokerAddrs) {
        this.clusterName = clusterName;
        this.brokername = brokername;
        this.brokerAddrs = brokerAddrs;
    }
}

