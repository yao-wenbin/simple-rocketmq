package com.yaowb.messagestore;

import lombok.Data;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
@Data
public class MessageStoreConfig {

    BrokerRole role = BrokerRole.MASTER;

    int haListenPort = 0;

    public enum BrokerRole {
        MASTER,
        SLAVE,
        ;

        public boolean isMaster() {
            return this.equals(MASTER);
        }

        public boolean isSlave() {
            return this.equals(SLAVE);
        }
    }


}





