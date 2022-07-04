package me.heartalborada.listener;

import com.google.gson.Gson;
import me.heartalborada.Main;
import me.heartalborada.config;
import me.heartalborada.utils.size;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.lang.String.format;

@SuppressWarnings("uncheck")
public class tencentListener implements ListenerHost {
    private static final Logger logger = LogManager.getLogger();
    private static EventChannel<Event> channel;
    public tencentListener(){
        channel = GlobalEventChannel.INSTANCE.filter(event -> event instanceof BotEvent && ((BotEvent) event).getBot().getId() == config.botID);
        channel.exceptionHandler(e -> {logger.error(e.getMessage());return null;} );
        channel.registerListenerHost(this);
    }

    @EventHandler
    public void OnGroupMessage(GroupMessageEvent g){
        MessageChain msg = g.getMessage();
        Group group = g.getGroup();
        Member member = g.getSender();
        Map<String,String> map = new LinkedHashMap<>();
        StringBuilder conMsg = new StringBuilder();
        for (SingleMessage sm :msg) {
            if (sm instanceof At) {
                conMsg.append(format("@%d ", ((At) sm).getTarget()));
            } else if (sm instanceof Image) {
                Image img = (Image)sm;
                conMsg.append(format("[Image](height: %d; width: %d; size: %s; isEmoji:%b) ",img.getHeight(),img.getWidth(), size.getSize(img.getSize()),img.isEmoji()));
            } else if (sm instanceof AtAll) {
                conMsg.append("@All ");
            } else if (sm instanceof Face) {
                Face f = (Face)sm;
                conMsg.append(format("[Face](name:%s) ",f.getName()));
            } else if (sm instanceof FlashImage) {
                conMsg.append("[FlashImage] ");
            } else if (sm instanceof PokeMessage) {
                conMsg.append("[PokeMessage] ");
            } else if (sm instanceof VipFace) {
                conMsg.append("[VipFace] ");
            } else if (sm instanceof LightApp) {
                conMsg.append("[LightApp] ");
            } else if (sm instanceof SimpleServiceMessage) {
                conMsg.append("[SimpleServiceMessage] ");
            } else if (sm instanceof Dice) {
                Dice d = (Dice)sm;
                conMsg.append(format("[Dice](Point: %d) ",d.getValue()));
            } else if (sm instanceof MusicShare) {
                MusicShare ms = (MusicShare)sm;
                conMsg.append(format("[MusicShare](Name: %s; Url: %S) ",ms.getTitle(),ms.getJumpUrl()));
            } else if (sm instanceof FileMessage) {
                FileMessage fl = (FileMessage)sm;
                conMsg.append(format("[FileMessage](Size:%s; Name:%s) ", size.getSize(fl.getSize()),fl.getName()));
            } else if (sm instanceof PlainText) {
                conMsg.append(sm).append(" ");
            }
        }
        map.put("message", conMsg.toString());
        map.put("group_name",group.getName());
        map.put("group_id", String.valueOf(group.getId()));
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
