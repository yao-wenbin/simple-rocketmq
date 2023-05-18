package com.yaowb.rocketmq.ha;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @Author yaowenbin
 * @Date 2023/5/18
 */
@Slf4j
public class DefaultHAConnection implements HAConnection{

    private final HAService haService;

    private final SocketChannel sc;

    private final String clientAddress;
    private final WriteSocketService writeSocketService;
    private final ReadSocketService readSocketService;

    private volatile HAConnectionState state = HAConnectionState.TRANSFER;
    private volatile long slaveRequestOffset = -1;
    private volatile long slaveAckOffset = -1;


    public DefaultHAConnection(final HAService haService, final SocketChannel sc) throws Exception {
        this.haService = haService;
        this.sc = sc;
        this.clientAddress = this.sc.socket().getRemoteSocketAddress().toString();
        this.sc.configureBlocking(false);
        this.sc.socket().setSoLinger(false, -1);
        this.sc.socket().setTcpNoDelay(true);
        this.writeSocketService = new WriteSocketService(this.sc);
        this.readSocketService = new ReadSocketService(this.sc);
    }

    @Override
    public void start() {
        changeCurrentState(HAConnectionState.TRANSFER);
        this.readSocketService.start();
        this.writeSocketService.start();
    }

    @Override
    public void shutdown() {
        changeCurrentState(HAConnectionState.SHUTDOWN);
        this.readSocketService.shutdown(true);
        this.writeSocketService.shutdown(true);
        this.close();
    }

    private void changeCurrentState(HAConnectionState newState) {
        this.state = newState;
    }

    public void close() {
        if (this.sc != null) {
            try {
                this.sc.close();
            } catch (IOException e) {
                log.error("HA Connection socket channel shutdown  has exception", e);
            }
        }
    }

    public enum HAConnectionState {
        TRANSFER,
        SHUTDOWN,
        ;
    }
}
