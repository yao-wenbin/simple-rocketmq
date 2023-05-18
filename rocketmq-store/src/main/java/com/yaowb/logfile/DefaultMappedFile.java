package com.yaowb.logfile;

import com.yaowb.rocketmq.common.utils.Message;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author yaowenbin
 * @Date 2023/5/5
 */
@Slf4j
public class DefaultMappedFile implements MappedFile{

    public static final int OS_PAGE_SIZE;

    protected static final AtomicLong TOTAL_MAPPED_MEMORIES;
    protected static final AtomicInteger TOTAL_MAPPED_FILES;

    protected static final AtomicIntegerFieldUpdater<DefaultMappedFile> WROTE_POSITION_UPDATER;
    protected static final AtomicIntegerFieldUpdater<DefaultMappedFile> COMMITTED_POSITION_UPDATER;
    protected static final AtomicIntegerFieldUpdater<DefaultMappedFile> FLUSHED_POSITION_UPDATER;

    static {
        OS_PAGE_SIZE = 1024 * 4;
        TOTAL_MAPPED_MEMORIES = new AtomicLong(0);
        TOTAL_MAPPED_FILES = new AtomicInteger(0);

        WROTE_POSITION_UPDATER = AtomicIntegerFieldUpdater.newUpdater(DefaultMappedFile.class, "wrotePosition");
        COMMITTED_POSITION_UPDATER = AtomicIntegerFieldUpdater.newUpdater(DefaultMappedFile.class, "committedPosition");
        FLUSHED_POSITION_UPDATER = AtomicIntegerFieldUpdater.newUpdater(DefaultMappedFile.class, "flushedPosition");
    }

    protected volatile int wrotePosition;
    protected volatile int committedPosition;
    protected volatile int flushedPosition;

    protected ByteBuffer writeBuffer;
    protected File file;
    protected long fileOffset;
    protected String fileName;
    protected int fileSize;
    protected FileChannel fileChannel;

    protected final MappedByteBuffer mappedByteBuffer;
    private long storeTimestamp;


    public DefaultMappedFile(final String fileName, final int filesize) throws IOException {
        this.fileSize = filesize;
        this.fileName = fileName;
        this.file = new File(fileName);
        this.fileOffset = Long.parseLong(file.getName());

        boolean success = false;
        try {
            this.fileChannel =  new RandomAccessFile(this.file, "rw").getChannel();
            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, this.fileSize);
            TOTAL_MAPPED_MEMORIES.addAndGet(this.fileSize);
            TOTAL_MAPPED_FILES.incrementAndGet();
            success = true;
        } catch (FileNotFoundException e) {
            log.error("Failed  to create file" + this.fileName, e);
            throw e;
        } finally {
            if (!success && this.fileChannel != null) {
                this.fileChannel.close();
            }
        }
    }



    @Override
    public AppendMessageResult appendMessage(@NonNull Message message, AppendMessageCallback cb) {
        int currentPosition = WROTE_POSITION_UPDATER.get(this);

        if (currentPosition > this.fileSize) {
            log.error("MappedFile.appendMessage return null, because wrote position: {} > file size: {}", currentPosition, this.fileSize);
            return AppendMessageResult.unknownError();
        }

        // TODO result batch message
        // TODO 理清为什么slice后需要position
        ByteBuffer byteBuffer = appendMessageBuffer().slice();
        byteBuffer.position(currentPosition);

        AppendMessageResult result = cb.doAppend(fileOffset, byteBuffer, this.fileSize - currentPosition, message);
        WROTE_POSITION_UPDATER.addAndGet(this, result.wroteBytes());
        this.storeTimestamp = result.storeTimestamp();

        return result;
    }

    private ByteBuffer appendMessageBuffer() {
        return this.writeBuffer != null ? this.writeBuffer : this.mappedByteBuffer;
    }

}
