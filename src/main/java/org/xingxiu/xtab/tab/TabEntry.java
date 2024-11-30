package org.xingxiu.xtab.tab;

import org.bukkit.entity.Player;

public class TabEntry {

    private final Player player;
    private final TabManager tabManager;

    public TabEntry(Player player, TabManager tabManager) {
        this.player = player;
        this.tabManager = tabManager;
    }

    // 这里可以添加更多与单个Tab条目相关的方法，比如更新特定条目的显示内容等
    public void update() {
        tabManager.updatePlayerTabEntry(player);
    }
}