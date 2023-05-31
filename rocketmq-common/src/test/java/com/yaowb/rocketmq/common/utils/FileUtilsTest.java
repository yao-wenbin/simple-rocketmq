package com.yaowb.rocketmq.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */
class FileUtilsTest extends UnitTest{

    @Test
    void isYmlFile() {
        assertTrue(FileUtils.isYmlFile("yes.yml"));
        assertTrue(FileUtils.isYmlFile("true.yaml"));
        assertTrue(FileUtils.isYmlFile("usr/local/true.yaml"));

        assertFalse(FileUtils.isYmlFile("true.yal"));
        assertFalse(FileUtils.isYmlFile("true.yarml"));
    }

}