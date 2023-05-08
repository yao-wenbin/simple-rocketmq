package com.yaowb.commitlog;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */
@Getter
@Accessors(fluent = true)
public class PutMessageResult {

    private Status status;

    private long wroteOffset;
    private int wroteBytes;

    PutMessageResult(Status status) {
        this.status = status;
    }

    public static PutMessageResult ok() {
        return new PutMessageResult(Status.OK);
    }

    public static PutMessageResult propertiesSizedExceed() {
        return new PutMessageResult(Status.PROPERTIES_SIZE_EXCEEDED);
    }



    enum Status{
        OK,

        PROPERTIES_SIZE_EXCEEDED;
    }
}
