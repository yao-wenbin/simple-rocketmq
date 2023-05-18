package com.yaowb.logfile;

import com.yaowb.rocketmq.common.utils.Message;

import java.nio.ByteBuffer;

/**
 * @Author yaowenbin
 * @Date 2023/5/5
 */
public interface AppendMessageCallback {

    AppendMessageResult doAppend(final long fileFromOffset, final ByteBuffer byteBuffer, final int maxBlank, final Message msg);

}
