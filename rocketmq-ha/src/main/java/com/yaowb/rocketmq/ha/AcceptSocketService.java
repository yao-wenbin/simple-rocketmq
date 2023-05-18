package com.yaowb.rocketmq.ha;

import java.io.IOException;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
public interface AcceptSocketService {
    void beginAccept() throws IOException;

    void start() throws Exception;

}
