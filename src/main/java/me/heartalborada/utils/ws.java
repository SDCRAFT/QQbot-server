package me.heartalborada.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ws extends WebSocketServer {
    private static final Logger logger = LogManager.getLogger("WebSocket");
    private static final List<WebSocket> list = new ArrayList<>();
    public ws(int port){
        super(new InetSocketAddress(port));
        this.start();
    }
    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info(webSocket.getRemoteSocketAddress() + " connected");
        list.add(webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.info(webSocket.getRemoteSocketAddress() + " disconnected");
        webSocket.close();
        list.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        logger.info("l收到消息：" + s + webSocket.getRemoteSocketAddress());
        if (StringUtils.substring(s, 0, 5).equals("leave")){
            String name = StringUtils.substring(s,5);
            logger.info(name + "leave the game");
        } else if (StringUtils.substring(s, 0, 4).equals("join")){
            String name = StringUtils.substring(s,4);
            logger.info(name + "join the game");
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.error(e);
        webSocket.close();
    }

    @Override
    public void onStart() {
        logger.info("WebSocket listening on " + this.getPort());
    }

    private void sendMsg(Object o) {
        for(WebSocket ws:list){
            ws.send(o.toString());
        }
    }
}
