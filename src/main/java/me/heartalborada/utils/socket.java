package me.heartalborada.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.*;

public class socket extends WebSocketServer {
    private static final Logger logger = LogManager.getLogger();
    private static final List<WebSocket> list = new ArrayList<>();

    public socket(int port) {
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
        logger.info("Receive message: " + s + webSocket.getRemoteSocketAddress());
        Map<String,String> map = new LinkedHashMap<>();
        try {
            JsonObject jb = JsonParser.parseString(s).getAsJsonObject();
            if(!jb.has("type")) {
                map.put("code","error");
                map.put("reason","cannot find 'type' key");
            } else {
                if (jb.get("type").getAsString().equals("join")) {
                    if(!jb.has("name")){
                        map.put("code","error");
                        map.put("reason","cannot find 'name' key");
                    } else {
                        String name = jb.get("name").getAsString();
                        map.put("code", "ok");
                    }
                } else if (jb.get("type").getAsString().equals("leave")) {
                    if(!jb.has("name")){
                        map.put("code","error");
                        map.put("reason","cannot find 'name' key");
                    } else {
                        String name = jb.get("name").getAsString();
                        map.put("code", "ok");
                    }
                } else if (jb.get("type").getAsString().equals("chat")) {
                    if(!jb.has("message")) {
                        map.put("code","error");
                        map.put("reason","cannot find 'message' key");
                    } else {
                        String name = jb.get("name").getAsString();
                        String msg = jb.get("message").getAsString();
                        map.put("code", "ok");
                    }
                } else if (jb.get("type").getAsString().equals("count")) {
                    //务必做时间戳比较,查询队列
                    if(!jb.has("count")) {
                        map.put("code","error");
                        map.put("reason","cannot find 'count' key");
                    } else if (!jb.has("time")){
                        map.put("code","error");
                        map.put("reason","cannot find 'time' key");
                    } else {
                        String count = jb.get("count").getAsString();
                        long time = jb.get("time").getAsLong();
                        map.put("code", "ok");
                    }
                } else {
                    map.put("code", "error");
                    map.put("reason", "unknown type");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            map.put("code", "error");
            map.put("reason", "it not a json");
        }
        webSocket.send(new Gson().toJson(map));
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

    public void sendMsg(Object o) {
        for (WebSocket ws : list) {
            ws.send(o.toString());
        }
    }
}
