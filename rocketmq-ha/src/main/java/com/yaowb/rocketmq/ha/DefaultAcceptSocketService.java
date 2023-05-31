package com.yaowb.rocketmq.ha;


import com.yaowb.messagestore.MessageStoreConfig;
import com.yaowb.rocketmq.common.concurrent.ServiceThread;
import com.yaowb.rocketmq.common.utils.NetworkUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @Author yaowenbin
 * Listen to slave connections to create {@link HAConnection}
 * 处理从节点连接请求，并且创建HAConnection.
 */
@Slf4j
public class DefaultAcceptSocketService extends ServiceThread implements AcceptSocketService {

    private final HAService haService;

    private final SocketAddress socketAddressListen;
    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    private final MessageStoreConfig messageStoreConfig;

    public DefaultAcceptSocketService(final HAService haService, final MessageStoreConfig messageStoreConfig) {
        this.haService = haService;
        this.messageStoreConfig = messageStoreConfig;
        this.socketAddressListen = new InetSocketAddress(messageStoreConfig.getHaListenPort());
    }


    @Override
    public void beginAccept() throws IOException {
        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.socket().setReuseAddress(true);
        this.serverSocketChannel.socket().bind(this.socketAddressListen);

        if (0 == messageStoreConfig.getHaListenPort()) {
            messageStoreConfig.setHaListenPort(this.serverSocketChannel.socket().getLocalPort());
            log.info("由操作系统选择HA端口: {}", messageStoreConfig.getHaListenPort());
        }
        this.serverSocketChannel.configureBlocking(false);
        this.selector = NetworkUtils.openSelector();
        this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public String getServiceName() {
        return "Default Accept Socket Service";
    }

    @Override
    public void run() {
        while (!isStopped()) {
            try {
                // 等待1秒
                this.selector.select(1000);

                // 一秒内接收到的请求
                Set<SelectionKey> selected = this.selector.selectedKeys();

                if (selected.isEmpty()) {
                    continue;
                }

                for (SelectionKey selectionKey : selected) {
                    // 请求是否可以接收
                    if (!selectionKey.isAcceptable()) {
                        log.warn("unexpected ops: selection key : {} is not acceptable", selectionKey);
                    }

                    // 创建套接字通道SocketChannel
                    SocketChannel sc = ((ServerSocketChannel) selectionKey.channel()).accept();
                    if (sc == null) {
                        continue;
                    }
                    log.info("HA Service reveive new connection : {}", sc.socket().getRemoteSocketAddress());

                    try {
                        // 封装为HAConnection
                        HAConnection conn = createConnection(sc);
                        conn.start();
                        // 加入到HAService中进行管理
                        haService.addConnection(conn);
                    } catch (Exception e) {
                        log.error("create HA Connection catch exception", e);
                        sc.close();
                    }

                }
            } catch (IOException e) {
                log.error("{} service has exception", getServiceName(), e);
            }
        }
    }

    HAConnection createConnection(SocketChannel sc) throws Exception{
        return new DefaultHAConnection(haService, sc);
    }
}
