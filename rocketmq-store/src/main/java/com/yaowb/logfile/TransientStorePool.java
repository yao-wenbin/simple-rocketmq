package com.yaowb.logfile;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @Author yaowenbin
 * @Date 2023/5/9
 */
@Slf4j
public class TransientStorePool {

    private final int poolSize = 5;

    // 1M
    private final int fileSize = 1024 * 1024;
    private final Deque<ByteBuffer> availableBuffers = new ConcurrentLinkedDeque<>();

    private volatile boolean isRealCommit = true;


    public void init() {
        for (int i = 0; i < poolSize; i++) {
            // replace jdk8's DirectBuffer. Using Unsafe to get Bytebuffer Address.
            // 替换掉JDK8中的DirectBuffer. 使用Unsafe方法来获得ByteBuffer的内存地址.
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(fileSize);
            long address = getUnsafe().getLong(byteBuffer, Unsafe.ARRAY_BYTE_BASE_OFFSET);
            // final long address = ((DirectBuffer) byteBuffer).address();
            // using native os function to lock memory address.
            // 使用本地操作系统的函数来锁定内存
            LibC.INSTANCE.mlock(new Pointer(address), new NativeLong(fileSize));

            availableBuffers.offer(byteBuffer);
        }
    }

    public static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception ignore) {

        }
        return null;
    }



}
