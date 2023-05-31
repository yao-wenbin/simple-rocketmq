package com.yaowb.rocketmq.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

    public static boolean isYmlFile(String fileName) {
        return fileName.endsWith(".yml") || fileName.endsWith(".yaml");
    }

    public static <T> T loadYml(String path, Class<T> clz) {
        try {
            FileInputStream fis = new FileInputStream(path);
            return new Yaml().loadAs(fis, clz);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
