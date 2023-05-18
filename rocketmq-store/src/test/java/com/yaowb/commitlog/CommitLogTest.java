package com.yaowb.commitlog;

import com.yaowb.rocketmq.common.utils.Message;
import com.yaowb.rocketmq.common.utils.UnitTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Author yaowenbin
 * @Date 2023/5/6
 */
class CommitLogTest extends UnitTest {

    MessageConfig config = new MessageConfig();
    CommitLog commitLog = new CommitLog(config);


    @Test
    void asyncPutMessage() throws ExecutionException, InterruptedException {
        Message msg = new Message();

        CompletableFuture<PutMessageResult> future = commitLog.asyncPutMessage(msg);
        PutMessageResult putMessageResult = future.get();

        assertThat(putMessageResult.status()).isEqualTo(PutMessageResult.Status.OK);
    }


}
