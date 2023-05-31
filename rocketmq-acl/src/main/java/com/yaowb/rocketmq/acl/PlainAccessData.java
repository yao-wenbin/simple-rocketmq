package com.yaowb.rocketmq.acl;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */
@Data
public class PlainAccessData {

    private List<String> globalWhiteRemoteAddress = new ArrayList<>();
    private List<PlainAccessAccount> accounts = new ArrayList<>();


}
