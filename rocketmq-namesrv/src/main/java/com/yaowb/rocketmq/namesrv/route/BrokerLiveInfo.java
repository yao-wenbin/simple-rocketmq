package com.yaowb.rocketmq.namesrv.route;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.channels.Channel;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
@Data
@NoArgsConstructor
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

    public boolean expired(long nowTimeMills) {
        return nowTimeMills - lastUpdateTimestamp > heartbeatTimeoutMills;
    }
}
