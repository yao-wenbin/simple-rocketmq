import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author yaowenbin
 * @Date 2023/5/6
 */
@Slf4j
class ByteBufferTest {

    @Test
    void slice() throws IOException {
        String filepath = "./temp.txt";

        File file = new File(filepath);
        FileChannel channel = new RandomAccessFile(file, "rw").getChannel();

        ByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 8);

        log.info("byteBuffer: {}", byteBuffer);
        byteBuffer.putChar('a');

        log.info("byteBuffer: {}", byteBuffer);
        log.info("byteBuffer' Position: {}", byteBuffer.position());

        ByteBuffer slicedBuffer = byteBuffer.slice();
        log.info("slicedBuffer: {}", slicedBuffer.position());
        slicedBuffer.putChar('b');

        // 调用了position后put c不会覆盖掉b，所以position也是按照sliced之后的current进行计算的，而不是之前的
        slicedBuffer.position(2);
        log.info("slicedBuffer: {}", slicedBuffer.position());
        slicedBuffer.putChar('c');

        ByteBuffer readerBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, 8);
        readerBuffer.position(0);
        while(readerBuffer.hasRemaining()) {
            System.out.println(readerBuffer.getChar());
        }
        channel.close();

        file.delete();

    }

}
