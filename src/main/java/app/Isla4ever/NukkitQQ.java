package app.Isla4ever;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class NukkitQQ extends PluginBase implements Listener {
    public Bot bot;
    public Long qq_number;
    public String qq_password;
    public boolean log;
    public boolean redirect_standard_input;
    public String[] qq_group;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        /*
        Bot config load state
         */
        Config config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);
        qq_number = config.getLong("qq_number");
        qq_password = config.getString("qq_password");
        log = config.getBoolean("debug_mode");
        redirect_standard_input = config.getBoolean("redirect_standard_input");
        qq_group = config.getString("qq_group").split(",");
/*
Not usable now
 */
//        if (redirect_standard_input) {
//            try {
//                System.setIn(new FileInputStream(new File("input.txt")));
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//        }

        this.getServer().getPluginManager().registerEvents(this, this);

        /*
        Bot login state
         */
        if (log) {
            bot = BotFactoryJvm.newBot(qq_number, qq_password, new BotConfiguration() {
                {
                    fileBasedDeviceInfo("deviceInfo.json");
                }
            });
        } else {
            bot = BotFactoryJvm.newBot(qq_number, qq_password, new BotConfiguration() {
                {
                    fileBasedDeviceInfo("deviceInfo.json");
                    noBotLog();
                    noNetworkLog();
                }
            });
        }
        bot.login();
        Events.registerEvents(bot, new SimpleListenerHost() {
            @EventHandler
            public ListeningStatus onGroupMessage(GroupMessageEvent event) {
                if (event.getSender().getId() != qq_number) {
                    for (int i = 0; i < qq_group.length; i++) {
                        if (String.valueOf(event.getGroup().getId()).equals(qq_group[i])) {
                            String message = event.getMessage().contentToString();
                            String sender = event.getSenderName();
                            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                                player.sendMessage("[QQ-" + sender + "]" + message);
                            }
                        }
                    }
                }
                return ListeningStatus.LISTENING;
            }
        });

        this.getLogger().info("NukkitQQ by Isla4ever Started");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("NukkitQQ disabled");
    }

    @cn.nukkit.event.EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage();
        for (int i = 0; i < qq_group.length; i++) {
            bot.getGroup(Long.parseLong(qq_group[i])).sendMessage("[" + event.getPlayer().getName() + "]" + message);
        }
    }
}
