package com.yaowb;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */
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

    enum Status{
        OK,
    }
}
