package com.yaowb.commitlog;

import com.yaowb.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */
@Slf4j
public class MessageEncoder {

    ByteBuf byteBuff;

    private int maxMessageBodySize;
    private int maxMessageSize;

    public MessageEncoder(final int maxBodySize) {
        int maxSize = Integer.MAX_VALUE - maxBodySize >= 64 * 1024
                ? maxMessageBodySize + 64 * 1024 : Integer.MAX_VALUE;

        ByteBufAllocator bufAllocator = UnpooledByteBufAllocator.DEFAULT;
        byteBuff = bufAllocator.directBuffer(maxMessageSize);

        this.maxMessageSize = maxSize;
        this.maxMessageBodySize = maxSize;
    }


    public ByteBuffer encode(Message msg) throws EncodeException {
        this.byteBuff.clear();

        final byte[]
                properties = msg.propertyString() == null ? null : msg.propertyString().getBytes(StandardCharsets.UTF_8);
        final int propertiesLen = properties == null ? 0 : properties.length;

        if (propertiesLen > Short.MAX_VALUE) {
            log.warn("\"putMessage properties length too long. length={}", propertiesLen);
            throw new EncodeException(PutMessageResult.propertiesSizedExceed());
        }

        final byte[] topic = msg.topic().getBytes(StandardCharsets.UTF_8);

        final int topicLength = topic.length;

        final int msgLen = msg.calcLenth();

        // 按照length的顺序写入数据
        // 1-5 is MsgLen, MagicCode, Body CRC, QueueId, Flag
        this.byteBuff.writeInt(msgLen);
        this.byteBuff.writeInt(msg.version().magicCode());
        this.byteBuff.writeInt(msg.bodyCRC());
        this.byteBuff.writeInt(msg.queueId());
        this.byteBuff.writeInt(msg.flag());

        // 6-10 QueueOffset, PhysicalOffset, SysFlag, BorTimestamp, bornHost.
        this.byteBuff.writeLong(msg.queueOffset());
        // PhysicalOffset will update later
        this.byteBuff.writeLong(0);
        this.byteBuff.writeInt(msg.sysFlag());
        this.byteBuff.writeLong(msg.bornTimestamp());
        this.byteBuff.writeBytes(msg.bornHost());

        // 11-15 is StoreTimestamp, StoreHost, ReconsumedTimes, preparedTransactionOffset, BodyLength
        this.byteBuff.writeLong(msg.storeTimestamp());
        this.byteBuff.writeBytes(msg.storeHostBytes());
        this.byteBuff.writeInt(msg.reconsumedTimes());
        this.byteBuff.writeLong(msg.preparedTransactionOffset());
        this.byteBuff.writeInt(msg.bodyLength());
        // 16 is Body Data.
        this.byteBuff.writeBytes(msg.body());
        // 17-18 is Topic Len and Topic Data.
        this.byteBuff.writeByte((byte)topicLength);
        this.byteBuff.writeBytes(topic);
        // 19-20 is Property Len and PropertyData.
        this.byteBuff.writeShort(propertiesLen);
        this.byteBuff.writeBytes(properties);
        return this.byteBuff.nioBuffer();
    }
}
