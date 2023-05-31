package com.yaowb.rocketmq.acl;

import com.yaowb.rocketmq.remoting.RemotingCommand;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */
public interface AccessValidator {

    /**
     * Parse reqeust to AccessResource
     * @param request
     * @param remoteAddr
     * @return
     */
    AccessResource parse(RemotingCommand request, String remoteAddr);


    /**
     * Validate the AccessResource
     * if AccessResource invalid, will throw exception.
     * @param accessResource
     */
    void validate(AccessResource accessResource);

}
