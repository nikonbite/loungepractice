package me.nikonbite.api.starter.bukkit.event;

import lombok.val;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.user.wrapper.DataWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        val id = DataWrapper.getId(event.getPlayer().getName()) == -1 ? GamerRegistry.insertId(event.getPlayer().getName()) :
                DataWrapper.getId(event.getPlayer().getName());
        val user = GamerRegistry.get(id);

        val group = DataWrapper.getGroup(id) == null ? GamerRegistry.insertGroup(user) : DataWrapper.getGroup(id);
        user.setGroup(group);
    }

    public void on(PlayerQuitEvent event) {
        val id = DataWrapper.getId(event.getPlayer().getName());
        val user = GamerRegistry.get(id);

        GamerRegistry.getUSER_MAP().remove(user.getId());
    }
}