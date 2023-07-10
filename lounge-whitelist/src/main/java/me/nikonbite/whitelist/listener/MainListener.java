package me.nikonbite.whitelist.listener;

import me.nikonbite.whitelist.LoungeWhitelist;
import me.nikonbite.whitelist.data.PlayerDataEntry;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Map;

public class MainListener implements Listener {

    @EventHandler
    public void on(ServerConnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Map<String, PlayerDataEntry> data = LoungeWhitelist.getInstance().getPlayerData().getData();
        PlayerDataEntry playerData = data.get(player.getName());

        if (playerData == null) {
            player.disconnect(new TextComponent("You are not whitelisted!"));
            data.put(player.getName(), new PlayerDataEntry(player.getName(), false, 0));
            LoungeWhitelist.getInstance().getPlayerData().saveAll(data);
        } else if (!playerData.isWhitelisted()) {
            player.disconnect(new TextComponent("You are not whitelisted!"));
        }
    }
}
