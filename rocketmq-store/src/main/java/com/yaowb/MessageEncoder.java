package com.yaowb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.nio.charset.StandardCharsets;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */
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


    public void encode(Message msg) throws EncodeException {
        this.byteBuff.clear();

        final byte[]
                properties = msg.propertyString() == null ? null : msg.propertyString().getBytes(StandardCharsets.UTF_8);

        if (properties.length > Short.MAX_VALUE) {
            throw new RuntimeException("putMessage properties length too long. length=" + properties.length);
        }

        final byte[] topic = msg.topic().getBytes(StandardCharsets.UTF_8);

        final int topicLength = topic.length;
        final int bodyLenght = msg.body().length;

        final int msgLen = msg.calcLenth();

        // 按照length的顺序写入数据
        this.byteBuff.writeInt(msgLen);
        this.byteBuff.writeInt(msg.version().magicCode());
        this.byteBuff.writeInt(msg.bodyCRC());
        this.byteBuff.writeInt(msg.queueId());
        this.byteBuff.writeInt(msg.flag());
        this.byteBuff.writeLong(msg.queueOffset());
        // PhysicalOffset will update later
        this.byteBuff.writeLong(0);
        this.byteBuff.writeInt(msg.sysFlag());

        this.byteBuff.writeLong(msg.bornTimestamp());
        this.byteBuff.writeBytes(msg.bornHost());

        this.byteBuff.writeLong(msg.storeTimestamp());
        this.byteBuff.writeBytes(msg.storeHostBytes());

        this.byteBuff.writeInt(msg.reconsumedTimes());

        this.byteBuff.writeLong(msg.preparedTransactionOffset());
        this.byteBuff.writeInt(msg.bodyLength());
        this.byteBuff.writeBytes(msg.body());
        this.byteBuff.writeByte((byte)topicLength);
        this.byteBuff.writeBytes(topic);
        this.byteBuff.writeShort(properties.length);
        this.byteBuff.writeBytes(properties);

    }
}
