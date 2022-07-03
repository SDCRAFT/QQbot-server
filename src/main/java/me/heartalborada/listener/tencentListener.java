package me.heartalborada.listener;

import com.google.gson.Gson;
import me.heartalborada.Main;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

@SuppressWarnings("uncheck")
public class tencentListener implements ListenerHost {
    private static final Logger logger = LogManager.getLogger("EventListen");
    @EventHandler
    public void OnGroupMessage(GroupMessageEvent g){
        MessageChain msg = g.getMessage();
        Group group = g.getGroup();
        Member member = g.getSender();
        Map<String,String> map = new LinkedHashMap<>();
        map.put("message", String.valueOf(msg));
        map.put("group_name",group.getName());
        map.put("group_id", String.valueOf(group.getId()));
        //这里可能有个bug
        map.put("member_name", member.getNameCard().equals("") ? member.getNick() : member.getNameCard());
        map.put("member_id", String.valueOf(member.getId()));
        Main.socket1.sendMsg(new Gson().toJson(map));
    }
    @EventHandler
    public void onBotOnline(BotOnlineEvent e){
        logger.info(format("%d online",e.getBot().getId()));
        logger.info(format(
                "has %d friend(s), %d groups",
                e.getBot().getFriends().size(),
                e.getBot().getGroups().size()
                )
        );
    }
    @EventHandler
    public void onMemberJoin(MemberJoinEvent e){
        MessageChainBuilder mc = new MessageChainBuilder();
        long id = e.getMember().getId();
        //这里以后会写文案
        mc.append(me.heartalborada.utils.time.getNow()).append(new At(id)).append("诞生了");
        e.getGroup().sendMessage(mc.build());
    }
    @EventHandler
    public void onMemberLeave(MemberLeaveEvent e){
        MessageChainBuilder mc = new MessageChainBuilder();
        long id = e.getMember().getId();
        if(e instanceof MemberLeaveEvent.Kick){
            //这里以后会写文案
            mc.append(me.heartalborada.utils.time.getNow()).append(new At(id)).append("爬了");
        } else {
            //这里以后会写文案
            mc.append(me.heartalborada.utils.time.getNow()).append(new At(id)).append("寄了");
        }
        e.getGroup().sendMessage(mc.build());
    }
}
