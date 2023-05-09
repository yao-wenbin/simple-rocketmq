package com.yaowb.rocketmq.namesrv.route;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */

@Data
@EqualsAndHashCode
public class BrokerAddrInfo {

    private String clusterName;
    private String brokerAddr;

    public BrokerAddrInfo(String clusterName, String brokerAddr) {
        this.clusterName = clusterName;
        this.brokerAddr = brokerAddr;
    }
}
