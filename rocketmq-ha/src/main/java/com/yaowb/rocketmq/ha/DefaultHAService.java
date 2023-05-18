package com.yaowb.rocketmq.ha;

import com.yaowb.messagestore.DefaultMessageStore;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
@Slf4j
public class DefaultHAService implements HAService{

    private final AtomicInteger connectionCount = new AtomicInteger(0);

    protected final List<HAConnection> connectionList = new LinkedList<>();

    protected AcceptSocketService acceptSocketService;

    protected GroupTransferService groupTransferService;

    protected DefaultMessageStore defaultMessageStore;

    // connect to master for slave.
    // 为了让从节点连接主节点
    protected HAClient haClient;

    // protected AtomicLong push2SlaveMaxOffset = new AtomicLong(0);


    @Override
    public void init(DefaultMessageStore defaultMessageStore) throws IOException {
        this.defaultMessageStore = defaultMessageStore;
        this.acceptSocketService = new DefaultAcceptSocketService(this, defaultMessageStore.getConfig());

        this.groupTransferService = new GroupTransferService(this);

        if (this.defaultMessageStore.getConfig().getRole().isSlave()) {
            haClient = new DefaultHAClient(defaultMessageStore);
        }
    }

    @Override
    public void start() throws Exception {
        this.acceptSocketService.beginAccept();
        this.acceptSocketService.start();
        this.groupTransferService.start();
        if (this.haClient != null) {
            this.haClient.start();
        }

    }

    @Override
    public void shutdown() throws Exception {

    }

    @Override
    public void addConnection(HAConnection conn) {
        synchronized (this.connectionList){
            this.connectionList.add(conn);
        }
    }
}
