package com.yaowb.rocketmq.common.utils;

import java.io.IOException;
import java.nio.channels.Selector;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
public class NetworkUtils {


    public static final String OS_NAME = System.getProperty("os.name");

    public static boolean isLinux() {
        return OS_NAME != null && OS_NAME.toLowerCase().contains("linux");
    }

    public static Selector openSelector() throws IOException {
        Selector result = null;
        if (isLinux()) {

        }
        return result;
    }

}
