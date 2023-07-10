package me.nikonbite.api.starter.bungee;

import lombok.Getter;
import me.nikonbite.api.LoungeAPI;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
public class BungeeAPIStarter extends Plugin {

    @Getter
    private static BungeeAPIStarter instance;
    private LoungeAPI loungeAPI;

    @Override
    public void onEnable() {
        instance = this;

        loungeAPI = new LoungeAPI();
        loungeAPI.init(this);
    }
}
