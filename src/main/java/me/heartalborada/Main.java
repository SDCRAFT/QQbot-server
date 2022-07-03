package me.heartalborada;

import me.heartalborada.utils.dependent;
import me.heartalborada.utils.ws;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static me.heartalborada.Config.LibrariesDir;
import static me.heartalborada.Config.mvnRepo;

public class Main {
    private static final Logger logger = LogManager.getLogger("Main");
    public static void main(String[] args) {
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
            dependent.loadLibraryClassMaven(
                    "com.google.code.gson",
                    "gson",
                    "2.9.0",
                    "",
                    mvnRepo,
                    LibrariesDir
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ws ws1 = new ws(1145);

    }
}