package com.yaowb.rocketmq.namesrv.route;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author yaowenbin
 * @Date 2023/5/10
 */
@Data
@AllArgsConstructor
public class UnRegisterBroekrRequest {

    private final String brokerName;
    private final String brokerAddr;
    private final String clusterName;

}
