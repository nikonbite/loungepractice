package me.nikonbite.whitelist;

import lombok.Getter;
import lombok.Setter;
import me.nikonbite.whitelist.command.WhitelistCommand;
import me.nikonbite.whitelist.data.PlayerData;
import me.nikonbite.whitelist.listener.MainListener;
import net.md_5.bungee.api.plugin.Plugin;

public final class LoungeWhitelist extends Plugin {

    @Getter
    private static LoungeWhitelist instance;
    @Getter
    private PlayerData playerData;
    @Getter
    @Setter
    private boolean whitelist;

    @Override
    public void onEnable() {
        instance = this;

        whitelist = true;

        playerData = new PlayerData();
        getProxy().getPluginManager().registerListener(this, new MainListener());
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand(playerData));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
