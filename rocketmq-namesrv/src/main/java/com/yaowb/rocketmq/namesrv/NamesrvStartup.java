package com.yaowb.rocketmq.namesrv;

import com.yaowb.MixAll;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @Author yaowenbin
 * @Date 2023/5/8
 */
@Slf4j
@Data
public class NamesrvStartup {

    private static Properties properties = null;
    private static NamesrvConfig namesrvConfig = null;
    private static NettyServerConfig nettyServerConfig = null;
    private static NettyClientConfig nettyClientConfig = null;
    private static ControllerConfig controllerConfig = null;

    public static void main(String... args) throws IOException {
        loadProperties(args);
        // createNamesrvController();
        // namesrvController.start();
        //
        // createControllerManager();
        log.info("start success");
    }

    public static void loadProperties(String... args) throws IOException {
        CommandLine commandLine = buildCommandLine(args);

        namesrvConfig = new NamesrvConfig();
        nettyServerConfig = new NettyServerConfig();
        nettyClientConfig = new NettyClientConfig();
        controllerConfig = new ControllerConfig();
        if (commandLine.hasOption('c')) {
            String file = commandLine.getOptionValue('c');
            if (file.isBlank()) {
                log.error("cannot open file from [{}]",file);
            } else {
                InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(file)));
                properties = new Properties();
                properties.load(in);

                MixAll.properties2Object(properties, namesrvConfig);
                MixAll.properties2Object(properties, nettyServerConfig);
                MixAll.properties2Object(properties, nettyClientConfig);
                MixAll.properties2Object(properties, controllerConfig);

                log.info("load config properties file Ok, {}", file);
                in.close();
            }
        }

        // if command line has args, override it to property.
        MixAll.commandline2Object(commandLine, namesrvConfig);
    }

    private static CommandLine buildCommandLine(String[] args) {
        CommandLine commandLine;
         try {
             Options options = new Options();
             // build option to parse, and then you can assign config location with -c namesrv.conf
             // 构建启动命令行的参数， 随后可以通过-c namesrv.conf的方式来制定配置文件
             options.addOption(
                     new Option("c", "configFile", true, "Name server config properties file"));

             DefaultParser parser = new DefaultParser();
             commandLine = parser.parse(options, args);
             // -h commoand for output helper
             if (commandLine.hasOption('h')) {
                 HelpFormatter hf = new HelpFormatter();
                 hf.setWidth(110);
                 hf.printHelp("mqnamesrv", options, true);
                 System.exit(-1);
             }
         } catch (ParseException e) {
             log.error("command line parse exception: ", e);
             System.exit(-1);
             return null;

         }
         return commandLine;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static NamesrvConfig getNamesrvConfig() {
        return namesrvConfig;
    }

    public static NettyServerConfig getNettyServerConfig() {
        return nettyServerConfig;
    }

    public static NettyClientConfig getNettyClientConfig() {
        return nettyClientConfig;
    }

    public static ControllerConfig getControllerConfig() {
        return controllerConfig;
    }
}
