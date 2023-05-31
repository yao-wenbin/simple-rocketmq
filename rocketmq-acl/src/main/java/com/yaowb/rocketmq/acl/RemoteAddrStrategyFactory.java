package com.yaowb.rocketmq.acl;

import cn.hutool.core.util.StrUtil;

/**
 * @Author yaowenbin
 * @Date 2023/5/31
 */
public class RemoteAddrStrategyFactory {

    public static final BlankRemoteAddrStrategy BLANK_STRATEGY = new BlankRemoteAddrStrategy();

    public RemoteAddrStrategy getStrategy(String remoteAddr) {
        if (StrUtil.isBlank(remoteAddr)) {
            return BLANK_STRATEGY;
        }

        return BLANK_STRATEGY;
    }


    public static class BlankRemoteAddrStrategy implements RemoteAddrStrategy {
        @Override
        public boolean match(String remoteAddr) {
            return true;
        }
    }



}
