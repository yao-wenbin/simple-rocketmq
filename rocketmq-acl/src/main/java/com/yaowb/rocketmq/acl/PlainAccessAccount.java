package com.yaowb.rocketmq.acl;

import lombok.Data;

import java.util.List;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */

@Data
public class PlainAccessAccount {

    private String accessKey;

    private String secretKey;

    private String whiteRemoteAddress;

    private boolean admin;

    private String defaultTopicPerm;
    private String defaultGroupPerm;

    private List<String> topicPerms;
    private List<String> groupPerms;

}
