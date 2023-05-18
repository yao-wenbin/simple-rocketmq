package com.yaowb.rocketmq.ha;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
public class GroupTransferService {

    private HAService haService;

    public GroupTransferService(HAService haService) {
        this.haService = haService;
    }

    public void start() {
    }
}
