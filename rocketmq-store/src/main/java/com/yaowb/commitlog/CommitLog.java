package com.yaowb.commitlog;

import com.yaowb.Message;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */
public class CommitLog {

    private PutMessageThreadLocal putMessageThreadLocal;

    public CompletableFuture<PutMessageResult> asyncPutMessage(final Message msg) {

        MessageEncoder encoder = putMessageThreadLocal.encoder();

        synchronized (this) {
            try {
                encoder.encode(msg);
            } catch (EncodeException e) {
                return CompletableFuture.completedFuture(e.result());
            }

        }
        return CompletableFuture.completedFuture(PutMessageResult.ok());

    }

    @Getter
    @Accessors(fluent = true)
    static class PutMessageThreadLocal {
        private final MessageEncoder encoder;
        private StringBuilder keyBuilder;

        PutMessageThreadLocal(int size) {
            encoder = new MessageEncoder(size);
            keyBuilder = new StringBuilder();
        }

    }

}
