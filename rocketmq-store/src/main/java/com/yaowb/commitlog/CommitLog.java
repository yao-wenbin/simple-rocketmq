package com.yaowb.commitlog;

import com.yaowb.logfile.AppendMessageCallback;
import com.yaowb.logfile.AppendMessageResult;
import com.yaowb.rocketmq.common.utils.Message;
import com.yaowb.rocketmq.common.utils.SysFlag;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */
public class CommitLog {

    public final static int BLANK_MAGIC_CODE = -875286124;

    private ThreadLocal<PutMessageThreadLocal> putMessageThreadLocal;

    private DefaultAppendMessageCallback appendMessageCallback = new DefaultAppendMessageCallback();

    public CommitLog(MessageConfig config) {
        putMessageThreadLocal = ThreadLocal.withInitial(() ->
                new PutMessageThreadLocal(config.maxMessageSize()));

    }

    public CompletableFuture<PutMessageResult> asyncPutMessage(final Message msg) {

        MessageEncoder encoder = putMessageThreadLocal.get().encoder();

        synchronized (this) {
            try {
                msg.encodedBuff(encoder.encode(msg));
            } catch (EncodeException e) {
                return CompletableFuture.completedFuture(e.result());
            }

        }
        return CompletableFuture.completedFuture(PutMessageResult.ok());

    }

    class DefaultAppendMessageCallback implements AppendMessageCallback {

        private static final int END_FILE_MIN_BLACK_LEN = 4 + 4;

        private final ByteBuffer msgStoreItemMemory;

        DefaultAppendMessageCallback() {
            msgStoreItemMemory = ByteBuffer.allocate(END_FILE_MIN_BLACK_LEN);
        }

        @Override
        public AppendMessageResult doAppend(final long fileFromOffset, final ByteBuffer byteBuffer,
                                            final int maxBlank, final Message msg) {

            long wroteOffset = fileFromOffset +byteBuffer.position();
            long queueOffset = msg.queueOffset();
            ByteBuffer preBuffer = msg.encodedBuff();
            final int msgLen = preBuffer.get(0);

            // Check For Sufficient Free Space
            if (msgLen + END_FILE_MIN_BLACK_LEN > maxBlank) {
                this.msgStoreItemMemory.clear();
                this.msgStoreItemMemory.putInt(maxBlank);
                this.msgStoreItemMemory.putInt(CommitLog.BLANK_MAGIC_CODE);

                byteBuffer.put(this.msgStoreItemMemory.array(), 0, 8);
                return AppendMessageResult.endOfFile(wroteOffset, maxBlank, msg.storeTimestamp(), queueOffset);
            }

            // Do Update The MsgBuffer and Put It to Storage.

            // // 1-5 is MsgLen, MagicCode, Body CRC, QueueId, Flag
            int pos = 4 + 4 + 4 + 4 + 4;
            // 6 Update QueueOffset
            preBuffer.putLong(pos, queueOffset);
            pos += 8;
            // 7 Update PhysicalOffset
            preBuffer.putLong(pos, fileFromOffset + byteBuffer.position());
            int ipLen = SysFlag.bornHostIsV4(msg.sysFlag()) ? 4 + 4 : 16 + 4;
            // 8 SysFlag, 9 BornTimestamp, 10 BornHost, 11 StoreTimestamp
            pos += 8 + 4 + 8 + ipLen;
            // Update 11.StoreTimestamp
            preBuffer.putLong(pos, msg.storeTimestamp());

            // Put it to MappedFile's ByteBuffer then persistent msg.
            byteBuffer.put(preBuffer);
            msg.clearByteBuffer();

            return AppendMessageResult.ok(wroteOffset, msgLen, msg.storeTimestamp(), queueOffset);
        }

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
