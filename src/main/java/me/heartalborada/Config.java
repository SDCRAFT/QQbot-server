package me.heartalborada;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    public static String path = System.getProperty("usr.dir");
    public static Logger logger = LogManager.getLogger();
}
