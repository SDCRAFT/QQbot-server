package me.heartalborada.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class ws extends WebSocketServer {
    private static Logger logger = LogManager.getLogger("WebSocket");
    private static List<WebSocket> list = new ArrayList<WebSocket>();
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
        list.remove(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        logger.info("收到消息：" + s + webSocket.getRemoteSocketAddress());
        if (s.equals("onDisconnect")){
            this.sendmsg("test");
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

    private void sendmsg(Object o) {
        for(WebSocket ws:list){
            ws.send(o.toString());
        }
    }
}
