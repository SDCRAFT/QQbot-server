package me.heartalborada;

import me.heartalborada.utils.dependent;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static me.heartalborada.Config.LibrariesDir;

public class Main {
    public static void main(String[] args) {
        if(!LibrariesDir.exists() && !LibrariesDir.mkdirs()) {
            throw new RuntimeException("Failed to create " + LibrariesDir.getPath());
        }
        try {
            String a = dependent.getLibraryVersionMaven("jakarta.websocket","jakarta.websocket-api","https://maven.aliyun.com/repository/public","release");
            dependent.loadLibraryClassMaven("jakarta.websocket","jakarta.websocket-api",a,"","https://maven.aliyun.com/repository/public",LibrariesDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
}