package app.Isla4ever;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import kotlin.coroutines.Continuation;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import net.mamoe.mirai.utils.LoginSolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class NukkitQQ extends PluginBase implements Listener {
    public Bot bot;
    public Long qq_number;
    public String qq_password;
    public boolean log;
    public boolean redirect_standard_input;
    public String[] qq_group;
    public File datafolder;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        int pluginId = 8706;
        Metrics metrics = new Metrics(this, pluginId);
        datafolder = this.getDataFolder();
        /*
        Bot config load state
         */
        Config config = new Config(datafolder + "/config.yml", Config.YAML);
        qq_number = config.getLong("qq_number");
        qq_password = config.getString("qq_password");
        log = config.getBoolean("debug_mode");
        redirect_standard_input = config.getBoolean("nogui_support");
        qq_group = config.getString("qq_group").split(",");

        this.getServer().getPluginManager().registerEvents(this, this);
        LoginSolver loginsolver = new LoginSolver() {
            @Nullable
            @Override
            public Object onSolvePicCaptcha(@NotNull Bot bot, @NotNull byte[] bytes, @NotNull Continuation<? super String> continuation) {
                final String[] return_data = new String[1];
                return_data[0] = "";
                try {
                    Server.getInstance().getLogger().info("需要图片验证码登录, 验证码为 4 字母");
                    Server.getInstance().getLogger().info("验证码已保存到/插件目录/temp/captcha_picture.png");
                    Server.getInstance().getLogger().info("请在/插件目录/temp/captcha_answer.txt中输入，并等待");
                    File answer = new File(datafolder + "/temp/captcha_answer.txt");
                    if (!answer.exists()) {
                        File dir = new File(answer.getParent());
                        dir.mkdir();
                        answer.createNewFile();
                    }
                    File picture = new File(datafolder + "/temp/captcha_picture.png");
                    FileOutputStream fos = new FileOutputStream(picture);
                    fos.write(bytes);
                    fos.flush();
                    fos.close();
                    TimerTask task = new FileWatcher(answer) {
                        @Override
                        protected void onChange(File file) {
                            try {
                                Server.getInstance().getLogger().info("文件更改");
                                BufferedReader in = new BufferedReader(new FileReader(answer));
                                return_data[0] = in.readLine();
                                in.close();
                                picture.delete();
                                answer.delete();
                            } catch (IOException e) {
                            }
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, new Date(), 200);
                    while (return_data[0].length() == 0) {
                        Server.getInstance().getLogger().info("文件未更改，等待10s");
                        TimeUnit.SECONDS.sleep(10);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                return return_data[0];
            }

            @Nullable
            @Override
            public Object onSolveSliderCaptcha(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
                Server.getInstance().getLogger().info("需要滑动验证码，请在任意浏览器中打开以下链接并完成验证码");
                Server.getInstance().getLogger().info("完成后请更改/插件目录/temp/captcha_state.txt第一行为任意字符");
                try {
                    final String[] return_data = new String[1];
                    return_data[0] = "";
                    File answer = new File(datafolder + "/temp/captcha_state.txt");
                    if (!answer.exists()) {
                        File dir = new File(answer.getParent());
                        dir.mkdir();
                        answer.createNewFile();
                    }
                    TimerTask task = new FileWatcher(answer) {
                        @Override
                        protected void onChange(File file) {
                            try {
                                Server.getInstance().getLogger().info("文件更改");
                                BufferedReader in = new BufferedReader(new FileReader(answer));
                                return_data[0] = in.readLine();
                                in.close();
                                answer.delete();
                            } catch (IOException e) {
                            }
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, new Date(), 200);
                    while (return_data[0].length() == 0) {
                        Server.getInstance().getLogger().info("文件未更改，等待10s");
                        TimeUnit.SECONDS.sleep(10);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                return "finished";
            }

            @Nullable
            @Override
            public Object onSolveUnsafeDeviceLoginVerify(@NotNull Bot bot, @NotNull String url, @NotNull Continuation<? super String> continuation) {
                Server.getInstance().getLogger().info("需要进行账户安全认证");
                Server.getInstance().getLogger().info("该账户有[设备锁]/[不常用登录地点]/[不常用设备登录]的问题");
                Server.getInstance().getLogger().info("完成以下账号认证即可成功登录");
                Server.getInstance().getLogger().info(url);
                Server.getInstance().getLogger().info("完成后请更改/插件目录/temp/captcha_state.txt第一行为任意字符");
                try {
                    final String[] return_data = new String[1];
                    return_data[0] = "";
                    File answer = new File(datafolder + "/temp/captcha_state.txt");
                    if (!answer.exists()) {
                        File dir = new File(answer.getParent());
                        dir.mkdir();
                        answer.createNewFile();
                    }
                    TimerTask task = new FileWatcher(answer) {
                        @Override
                        protected void onChange(File file) {
                            try {
                                Server.getInstance().getLogger().info("文件更改");
                                BufferedReader in = new BufferedReader(new FileReader(answer));
                                return_data[0] = in.readLine();
                                in.close();
                                answer.delete();
                            } catch (IOException e) {
                            }
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, new Date(), 200);
                    while (return_data[0].length() == 0) {
                        Server.getInstance().getLogger().info("文件未更改，等待10s");
                        TimeUnit.SECONDS.sleep(10);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                return "finished";
            }
        };

        /*
        Bot login state
         */
        if (!redirect_standard_input) {
            if (log) {
                bot = BotFactoryJvm.newBot(qq_number, qq_password, new BotConfiguration() {
                    {
                        fileBasedDeviceInfo(datafolder+"/deviceInfo.json");
                        setLoginSolver(loginsolver);
                    }
                });
            } else {
                bot = BotFactoryJvm.newBot(qq_number, qq_password, new BotConfiguration() {
                    {
                        fileBasedDeviceInfo(datafolder+"/deviceInfo.json");
                        noBotLog();
                        noNetworkLog();
                    }
                });
            }
        } else {
            if (log) {
                bot = BotFactoryJvm.newBot(qq_number, qq_password, new BotConfiguration() {
                    {
                        fileBasedDeviceInfo(datafolder+"/deviceInfo.json");
                    }
                });
            } else {
                bot = BotFactoryJvm.newBot(qq_number, qq_password, new BotConfiguration() {
                    {
                        fileBasedDeviceInfo(datafolder+"/deviceInfo.json");
                        noBotLog();
                        noNetworkLog();
                    }
                });
            }
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
                                player.sendMessage("§e[QQ-" + sender + "] §2" + message);
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
    public void onPlayerChat (PlayerChatEvent event) {
        String message = event.getMessage();
        for (int i = 0; i < qq_group.length; i++) {
            bot.getGroup(Long.parseLong(qq_group[i])).sendMessage("[" + event.getPlayer().getName() + "]" + message);
        }
    }
    @cn.nukkit.event.EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        String name = event.getPlayer().getName();
        for (int i = 0; i < qq_group.length; i++) {
            bot.getGroup(Long.parseLong(qq_group[i])).sendMessage("玩家" + name + "加入了服务器");
        }
    }
    @cn.nukkit.event.EventHandler
    public void onPlayerQuit (PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        for (int i = 0; i < qq_group.length; i++) {
            bot.getGroup(Long.parseLong(qq_group[i])).sendMessage("玩家" + name + "退出了服务器");
        }
    }
}
