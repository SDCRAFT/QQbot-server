package me.heartalborada;

import java.io.File;

public class config {
    public static final String path = System.getProperty("usr.dir");
    public static final File librariesDir = new File(path,"libs");
    public static final String mvnRepo = "https://maven.aliyun.com/repository/public";
    public static File miraiDir = new File(path,"mirai");
    public static long botID= 0;
    public static short port = 1145;
}
