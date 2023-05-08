package com.yaowb.rocketmq.namesrv;

import com.yaowb.UnitTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author yaowenbin
 * @Date 2023/5/8
 */
class NamesrvStartupTest extends UnitTest {

    public final String ROCKET_HOME = System.getProperty("java.io.tmpdir");

    public final String NAMESRV_CONFIG_HOME = ROCKET_HOME + "namesrv.conf";

    @Test
    void startup() throws IOException {
        BufferedWriter writer = new BufferedWriter(Files.newBufferedWriter(Paths.get(NAMESRV_CONFIG_HOME)));
        writer.write("clientRequestThreadPoolNums=1");
        writer.newLine();
        writer.write("clientRequestThreadPoolQueueCapacity=300");
        writer.close();

        NamesrvStartup.loadProperties("-c".concat(NAMESRV_CONFIG_HOME));

        Assertions.assertThat(NamesrvStartup.getNamesrvConfig().getClientRequestThreadPoolNums()).isEqualTo(1);
        Assertions.assertThat(NamesrvStartup.getNamesrvConfig().getClientRequestThreadPoolQueueCapacity()).isEqualTo(300);
    }

}
