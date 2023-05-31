package com.yaowb.rocketmq.acl;

import com.yaowb.rocketmq.common.utils.UnitTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */
class AclFileWatchServiceTest extends UnitTest {

    private String aclDir = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    @Test
    void loadResourceDir() {
        URL resource = getClass().getClassLoader().getResource("acl/acl_1.yml");
        assertNotNull(resource);
        File file = new File(resource.getFile());
        assertTrue(file.exists());
    }


    @Test
    void shouldLoadFile() {

    }

}