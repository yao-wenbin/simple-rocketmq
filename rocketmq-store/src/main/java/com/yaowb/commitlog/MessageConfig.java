package com.yaowb.commitlog;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @Author yaowenbin
 * @Date 2023/5/6
 */
@Getter
@Accessors(fluent = true)
public class MessageConfig {

    private int maxMessageSize = 4 * 1024;

}
