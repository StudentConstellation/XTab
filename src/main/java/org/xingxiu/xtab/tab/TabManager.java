//package org.xingxiu.xtab.tab;
package org.xingxiu.xtab;

import org.xingxiu.xtab.tab.TabManager;

import org.bukkit.plugin.java.JavaPlugin;

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
        if (scoreboard!= null) {
            Bukkit.getScoreboardManager().removeScoreboard(scoreboard);
        }
    }

    private static class Config {
        boolean showPlayerAvatar;
        boolean showPlayerHealth;
        boolean showServerCpuUsage;
        boolean showServerMemoryUsage;
    }
}
