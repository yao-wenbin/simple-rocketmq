package com.yaowb.rocketmq.namesrv.route;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author yaowenbin
 * @Date 2023/5/11
 */

@Data
public class TopicRouteData {


    private List<BrokerData> brokerDataList = new ArrayList<>();

    private List<QueueData> queueDataList = new ArrayList<>();

    public void setBrokerDataList(Collection dataList) {
        brokerDataList = new ArrayList<>(dataList);
    }

    public void setQueueDataList(Collection dataList) {
        queueDataList = new ArrayList<>(dataList);
    }

}
