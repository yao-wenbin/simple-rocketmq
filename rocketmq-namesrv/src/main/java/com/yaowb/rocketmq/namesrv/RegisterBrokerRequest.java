package com.yaowb.rocketmq.namesrv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
@Builder
@Data
@AllArgsConstructor
public class RegisterBrokerRequest {

    private String brokerId;

    private String brokerName;

    private String clusterName;

}
