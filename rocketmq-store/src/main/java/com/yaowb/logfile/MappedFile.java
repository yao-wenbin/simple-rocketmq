package com.yaowb.logfile;

import com.yaowb.rocketmq.common.utils.Message;

/**
 * @Author yaowenbin
 * @Date 2023/5/5
 */
public interface MappedFile {


    AppendMessageResult appendMessage(Message message, AppendMessageCallback cb);


}
