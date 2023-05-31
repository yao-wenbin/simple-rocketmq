package com.yaowb.rocketmq.acl;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */
public interface RemoteAddrStrategy {

    /**
     *
     * @param remoteAddr
     * @return
     */
    boolean match(String remoteAddr);

}
