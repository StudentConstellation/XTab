package org.xingxiu.xtab.tab;

import com.google.gson.Gson;
import org.xingxiu.xtab.XTabPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 假设这里之前可能存在重复定义或者类声明相关问题，确保这里是正确的类声明起始
public class TabManager {

    private final XTabPlugin plugin;
    private Scoreboard scoreboard;
    private Objective objective;
    private final Map<Player, Team> playerTeams = new HashMap<>();
    private final Gson gson = new Gson();
    private final Config config;

    public TabManager(XTabPlugin plugin) {
        this.plugin = plugin;
        File configFile = new File(plugin.getDataFolder(), "config.json");
        try {
            config = gson.fromJson(new FileReader(configFile), Config.class);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to read config.json: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void initializeTab() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("xtab", "dummy", ChatColor.RED + "XTab");

        // 修正这里的错误，DisplaySlot.TAB是正确的常量名，之前可能写错了
        objective.setDisplaySlot(DisplaySlot.TAB);  

        for (Player player : Bukkit.getOnlinePlayers()) {
            createPlayerTeam(player);
            updatePlayerTabEntry(player);
        }

        startServerUsageUpdateTask();
    }

    private void createPlayerTeam(Player player) {
        Team team = scoreboard.registerNewTeam(player.getName());
        team.addEntry(player.getName());
        playerTeams.put(player, team);
    }

    private void updatePlayerTabEntry(Player player) {
        Team team = playerTeams.get(player);
        if (team == null) return;

        if (config.showPlayerAvatar) {
            // 这里假设你有获取玩家头像并设置为前缀的方法，暂时留空
            team.setPrefix("");
        }

        if (config.showPlayerHealth) {
            double health = player.getHealth();
            team.setSuffix(ChatColor.GREEN + "HP: " + health);
        }
    }

    private void startServerUsageUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (config.showServerCpuUsage || config.showServerMemoryUsage) {
                    // 这里假设你有获取服务器CPU和内存占用并格式化为字符串的方法，暂时留空
                    String serverUsageInfo = "";
                    if (config.showServerCpuUsage) {
                        serverUsageInfo += "CPU: [获取到的CPU占用率] ";
                    }
                    if (config.showServerMemoryUsage) {
                        serverUsageInfo += "Memory: [获取到的内存占用量]";
                    }

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Team team = playerTeams.get(player);
                        if (team!= null) {
                            team.setPrefix(ChatColor.YELLOW + serverUsageInfo);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void cleanupTab() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = playerTeams.get(player);
        if (team!= null) {
                scoreboard.resetScores(player.getName());
                team.unregister();
            }
        }
        if (objective!= null) {
            objective.unregister();
        }

        // 修正这里的错误，通过Bukkit.getScoreboardManager()获取的ScoreboardManager实例
        // 调用removeScoreboard方法时需要先获取当前Scoreboard实例的引用，然后再调用remove方法
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboard!= null) {
            scoreboardManager.getScoreboard(scoreboard.getName()).remove(scoreboard);
        }
    }

    private static class Config {
        boolean showPlayerAvatar;
        boolean showPlayerHealth;
        boolean showServerCpuUsage;
        boolean showServerMemoryUsage;
    }
}
