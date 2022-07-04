package me.heartalborada;

import me.heartalborada.listener.tencentListener;
import me.heartalborada.utils.librariesLoader;
import me.heartalborada.utils.login;
import me.heartalborada.utils.socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static me.heartalborada.config.*;

public class Main{
    private static final Logger logger = LogManager.getLogger();
    public static socket socket1 = null;
    public static void main(String[] args) {
        if (!librariesDir.exists() && !librariesDir.mkdirs()) {
            throw new RuntimeException("Failed to create " + librariesDir.getPath());
        }
        if (!miraiDir.exists() && !miraiDir.mkdirs()) {
            throw new RuntimeException("Failed to create " + miraiDir.getPath());
        }
        List<String[]> list = new LinkedList<>();
        list.add(new String[]{"org.slf4j", "slf4j-simple", "1.7.25", ""});
        list.add(new String[]{"org.slf4j", "slf4j-api", "1.7.25", ""});
        list.add(new String[]{"com.google.code.gson", "gson", "2.9.0", ""});
        list.add(new String[]{"org.java-websocket", "Java-WebSocket", "1.5.3", ""});
        list.add(new String[]{"net.mamoe","mirai-core-all","2.11.1","-all"});
        list.add(new String[]{"net.mamoe","mirai-logging-log4j2","2.11.1",""});
        list.add(new String[]{"org.yaml","snakeyaml","1.30",""});
        try {
            for(String[] strs : list) {
                librariesLoader.loadLibraryClassMaven(
                        strs[0],strs[1],strs[2],strs[3], mvnRepo, librariesDir
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        socket1 = new socket(config.port);
        tencentListener listener = new tencentListener();
    }
}