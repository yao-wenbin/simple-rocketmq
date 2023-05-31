package com.yaowb.rocketmq.namesrv;

import com.yaowb.rocketmq.common.utils.FileUtils;
import com.yaowb.rocketmq.common.utils.MixAll;
import lombok.Data;

/**
 * @Author yaowenbin
 * @Date 2023/5/8
 */
@Data
public class NamesrvConfig {

    /**
     * dir related config
     */
    // get from properties, and then get from env.
    // the former Constants means the key in Config file. the laster Constant means the key in Env.
    private String rocketmqHome = System.getProperty(MixAll.ROCKETMQ_HOME_PROPERTY, System.getenv(MixAll.ROCKETMQ_HOME_ENV));
    private String kvConfigPath = FileUtils.joinFilepath(System.getProperty("user.home"), "namesrv", "kvConfig.json");
    private String configStorePath = FileUtils.joinFilepath(System.getProperty("user.home"), "namesrv", "namesrv.properties");
    private String productEnvName = "center";


    private boolean clusterTest = false;
    private boolean orderMessageEnabled = false;
    private boolean returnOrderTopicConfigToBroker = true;

    private int clientRequestThreadPoolNums = 8;
    private int defaultThreadPoolNums = 16;
    private int clientRequestThreadPoolQueueCapacity = 5000;
    private int defaultThreadPoolQueueCapacity = 10000;
    private long scanNotActiveBrokerInterval = 5 * 1000;
    private int unRegisterBrokerQueueCapacity = 3000;
    // the slave can be an acting master when master node is down.
    private boolean supportActingMaster = false;
    private volatile boolean enableAllTopicList = true;
    private volatile boolean enableTopicList = true;
    private volatile boolean notifyMinBrokerIdChanged = false;
    // startController in namesrv. used for quick start demo.
    private boolean enableControllerInNamesrv = false;
    //
    private volatile boolean needWaitForService = false;
    private int waitSecondsForService = 45;

}
