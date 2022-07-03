package me.heartalborada;

import java.io.File;

public class config {
    public static String path = System.getProperty("usr.dir");
    public static File librariesDir = new File(path,"libs");
    public static String mvnRepo = "https://maven.aliyun.com/repository/public";
    public static File miraiDir = new File(path,"mirai");
}
