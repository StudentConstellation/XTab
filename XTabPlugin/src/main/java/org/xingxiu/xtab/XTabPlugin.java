package org.xingxiu.xtab;

import org.bukkit.plugin.java.JavaPlugin;

public class XTabPlugin extends JavaPlugin {

    private TabManager tabManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        tabManager = new TabManager(this);
        tabManager.initializeTab();
    }

    @Override
    public void onDisable() {
        if (tabManager!= null) {
            tabManager.cleanupTab();
        }
    }
}