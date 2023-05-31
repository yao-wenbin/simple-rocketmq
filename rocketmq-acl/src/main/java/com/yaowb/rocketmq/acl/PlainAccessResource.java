package com.yaowb.rocketmq.acl;

import com.yaowb.rocketmq.remoting.RemotingCommand;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */
public class PlainAccessResource implements AccessResource{

    public static PlainAccessResource parse(RemotingCommand request, String remoteAddr) {
        return new PlainAccessResource();
    }

}
