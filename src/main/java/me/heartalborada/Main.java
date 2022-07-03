package me.heartalborada;

import me.heartalborada.utils.dependent;
import me.heartalborada.utils.ws;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static me.heartalborada.Config.LibrariesDir;
import static me.heartalborada.Config.mvnRepo;

public class Main {
    private static Logger logger = LogManager.getLogger("Main");
    public static void main(String[] args) throws InterruptedException {
        if(!LibrariesDir.exists() && !LibrariesDir.mkdirs()) {
            throw new RuntimeException("Failed to create " + LibrariesDir.getPath());
        }
        try {
            dependent.loadLibraryClassMaven(
                    "org.slf4j",
                    "slf4j-simple",
                    "1.7.25",
                    "",
                    mvnRepo,
                    LibrariesDir
            );
            dependent.loadLibraryClassMaven(
                    "org.slf4j",
                    "slf4j-api",
                    "1.7.25",
                    "",
                    mvnRepo,
                    LibrariesDir
            );
            dependent.loadLibraryClassMaven(
                    "org.java-websocket",
                    "Java-WebSocket",
                    "1.5.3",
                    "",
                    mvnRepo,
                    LibrariesDir
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ws ws1 = new ws(11451);
        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = null;
            try {
                in = sysin.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ws1.broadcast(in);
            if (in.equals("exit")) {
                ws1.stop(1000);
                break;
            }
        }
    }
}