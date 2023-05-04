package com.yaowb;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @Author yaowenbin
 * @Date 2023/5/4
 */

@Getter
@Accessors(fluent = true)
public class Message {

    private MessageVersion version;

    private int bodyCRC;

    private int queueId;

    private int flag;

    private long queueOffset;

    private long bornTimestamp;

    private SocketAddress bornHost;

    private long storeTimestamp;

    private SocketAddress storeHost;

    private int reconsumedTimes;

    private String propertyString;

    private String topic;

    private int sysFlag;

    private byte[] body;


    private long preparedTransactionOffset;



    public int calcLenth() {
        int bornHostLength = SysFlag.bornHostIsV4(sysFlag) ? 8 : 20;
        int storeHostAddressLength = SysFlag.storeHostIsV4(sysFlag) ? 8 : 20;

        return 4 // TotalSize
            + 4 // MagicCode
            + 4 // BodyCRC
            + 4 // QueueId
            + 4 // Flag
            + 8 // QueueOffset
            + 8 // PhysicalOffset
            + 4 // SysFlag
            + 8 // BornTimestamp
            + bornHostLength // borHost
            + 8 // StoreTimestamp
            + storeHostAddressLength // sotreHostAddress
            + 4 // ReconsumeTimes
            + 8 // Prepared Transaction Offset
            + 4 + (Math.max(body().length, 0)) // BODY
            + version.topicLengthSize() + topicByteLength() // TOPIC
            + 2 + (Math.max(propertyByteLength(), 0)); // propertiesLength;
    }

    private int topicByteLength() {
        return topic.getBytes(StandardCharsets.UTF_8).length;
    }

    private int propertyByteLength() {
        return propertyString.getBytes(StandardCharsets.UTF_8).length;
    }


    public int bodyLength() {
        return body.length;
    }

    public byte[] storeHostBytes() {
        return socketAddress2ByteBuf(storeHost).array();
    }

    public byte[] bornHost() {
        return socketAddress2ByteBuf(bornHost).array();
    }

    // Now Only Support for Ipv4
    private ByteBuffer socketAddress2ByteBuf(SocketAddress socketAddress) {
        InetSocketAddress inetSocketAddress = ((InetSocketAddress) socketAddress);

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + 4);
        byteBuffer.put(inetSocketAddress.getAddress().getAddress(), 0, 4);
        byteBuffer.putInt(inetSocketAddress.getPort());
        // ByteBuffer::flip用于将当前缓冲区从写状态切换到读状态，以供后续读取。
        byteBuffer.flip();
        return byteBuffer;
    }


}
