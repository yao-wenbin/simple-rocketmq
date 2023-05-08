package com.yaowb.logfile;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author yaowenbin
 * @Date 2023/5/5
 */
@Data
@Accessors(fluent = true)
public class AppendMessageResult {

    Status status = Status.UNKNOWN_ERROR;

    private int wroteBytes = 0;

    private long wroteOffset = 0;

    private int msgLen = 0;

    private long storeTimestamp = 0;
    private long queueOffset = 0;



    public static AppendMessageResult unknownError() {
        return new AppendMessageResult();
    }

    public static AppendMessageResult ok(final long wroteOffset, final int msgLen,
                                         final long storeTimestamp, final long queueOffset) {
        return new AppendMessageResult().status(Status.OK)
                .wroteOffset(wroteOffset)
                .msgLen(msgLen)
                .storeTimestamp(storeTimestamp)
                .queueOffset(queueOffset);
    }

    public static AppendMessageResult endOfFile(final long wroteOffset, final int msgLen,
                                                final long storeTimestamp, final long queueOffset) {
        return new AppendMessageResult()
                .status(Status.END_OF_FILE)
                .wroteOffset(wroteOffset)
                .msgLen(msgLen)
                .storeTimestamp(storeTimestamp)
                .queueOffset(queueOffset);
    }


    public enum Status{
            UNKNOWN_ERROR,
            OK,
            END_OF_FILE,
            ;
    }

}
