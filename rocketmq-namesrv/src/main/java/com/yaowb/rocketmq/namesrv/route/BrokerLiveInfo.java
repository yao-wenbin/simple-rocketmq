package com.yaowb.rocketmq.namesrv.route;

import java.nio.channels.Channel;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
public class BrokerLiveInfo {

    private long lastUpdateTimestamp;
    private long heartbeatTimeoutMills;
    private DataVersion dataVersion;
    private Channel channel;

    public BrokerLiveInfo(long lastUpdateTimestamp, long heartbeatTimeoutMills, DataVersion dataVersion, Channel channel) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        this.heartbeatTimeoutMills = heartbeatTimeoutMills;
        this.dataVersion = dataVersion;
        this.channel = channel;
    }
}
