package com.yaowb.rocketmq.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @Author yaowenbin
 * @Date 2023/5/8
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    /**
     * join String with File separator
     */
    public static String joinFilepath(String... path) {
        return String.join(File.separator, path);
    }


}
