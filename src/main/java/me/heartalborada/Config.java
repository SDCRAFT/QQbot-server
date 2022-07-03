package me.heartalborada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Config {
    public static String path = System.getProperty("usr.dir");
    public static File LibrariesDir = new File(path,"libs");
    public static String mvnRepo = "https://maven.aliyun.com/repository/public";
}
