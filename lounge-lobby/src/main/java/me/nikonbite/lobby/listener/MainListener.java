package me.nikonbite.lobby.listener;

import me.nikonbite.lobby.LoungeLobby;
import me.nikonbite.lobby.ui.RegionSelectorUI;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutKickDisconnect;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class MainListener implements Listener {

    private final LoungeLobby plugin = LoungeLobby.getInstance();

    @EventHandler
    public void on(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        plugin.getLoungeBoard().setScoreboard(event.getPlayer());

        new RegionSelectorUI().showGUI(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        plugin.getLoungeBoard().removeScoreboard(event.getPlayer());
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        new RegionSelectorUI().showGUI((Player) event.getPlayer());
    }
}
