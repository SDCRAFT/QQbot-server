package me.heartalborada.utils;

import me.heartalborada.config;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;

import static me.heartalborada.config.miraiDir;

public class login {
    public static boolean doLogin(long id,String pw){
        File file = new File(config.botDir, String.valueOf(id));
        if (!file.exists() && !file.mkdirs()) {
            throw new RuntimeException("Failed to create " + file.getPath());
        }
        Bot bot = BotFactory.INSTANCE.newBot(
                id, pw,
                new BotConfiguration() {
                    {
                        fileBasedDeviceInfo();
                        setWorkingDir(file);
                        setCacheDir(config.cacheDir);
                    }
                }
        );
        bot.login();
        return bot.isOnline();
    }
    public static void autoLogin(){

    }
}
