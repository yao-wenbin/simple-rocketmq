package com.yaowb.logfile;

import com.yaowb.Message;
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

    protected ByteBuffer byteBuffer;
    protected File file;
    protected long fileOffset;
    protected String fileName;
    protected int fileSize;
    protected FileChannel fileChannel;

    protected final MappedByteBuffer mappedByteBuffer;



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
    public AppendMessageResult appendMessage(Message message, AppendMessageCallback cb) {
        return null;
    }

}
