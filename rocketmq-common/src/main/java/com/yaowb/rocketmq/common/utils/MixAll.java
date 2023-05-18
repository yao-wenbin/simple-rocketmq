package com.yaowb.rocketmq.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @Author yaowenbin
 * @Date 2023/5/8
 */
@Slf4j
public class MixAll {

    public static final String ROCKETMQ_HOME_PROPERTY = "rocketmq.home.dir";

    public static final String ROCKETMQ_HOME = "ROCKETMQ_HOME";
    public static long MASTER_ID = 0L;

    public static void properties2Object(Properties properties, Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            String mn = method.getName();
            if (!mn.startsWith("set")) {
                continue;
            }

            try {
                String firstChar = mn.substring(3, 4).toLowerCase();
                String keyNameWithoutFirstChar = mn.substring(4);

                String key = firstChar.concat(keyNameWithoutFirstChar);
                String property = properties.getProperty(key);
                if (property == null) {
                    continue;
                }

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes == null || parameterTypes.length == 0) {
                    continue;
                }
                String type = parameterTypes[0].getSimpleName();
                Object arg;
                if ("int".equals(type) || "Integer".equals(type)) {
                    arg = Integer.parseInt(property);
                } else if ("long".equals(type) || "Long".equals(type)) {
                    arg = Long.parseLong(property);
                } else if("double".equals(type) || "Double".equals(type)) {
                    arg = Double.parseDouble(property);
                } else if ("float".equals(type) || "Float".equals(type)) {
                    arg = Float.parseFloat(property);
                } else if ("String".equals(type)) {
                    arg = properties;
                } else {
                    log.warn("unexpeted property type: {}", type);
                    continue;
                }
                method.invoke(obj, arg);


            } catch (InvocationTargetException | IllegalAccessException e) {
                log.error("properties2Object error: ", e);
            }
        }
    }

    public static void commandline2Object(CommandLine commandLine, Object obj) {
        Properties commandLineProperties = new Properties();
        for (Option opt : commandLine.getOptions()) {
            String name = opt.getLongOpt();
            String val = commandLine.getOptionValue(name);
            if (val != null) {
                commandLineProperties.put(name, val);
            }
        }

        properties2Object(commandLineProperties, obj);
    }
}
