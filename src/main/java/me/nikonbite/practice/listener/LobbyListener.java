package me.nikonbite.practice.listener;

import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.practice.ui.EditorExtraItemsUI;
import me.nikonbite.practice.ui.EditorKitManagerUI;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Config;
import me.nikonbite.api.lib.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class LobbyListener implements Listener {

    @EventHandler
    public void on(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void on(FoodLevelChangeEvent event) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of((Player) event.getEntity());
        Statement state = loungePlayer.getStatement();

        if (state == Statement.LOBBY || state == Statement.EDITOR || state == Statement.QUEUE || state == Statement.SPECTATOR) {
            event.setCancelled(true);
        }

        if (loungePlayer.getParty() != null && state != Statement.PARTY_MATCH) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of((Player) event.getEntity());
        Statement state = loungePlayer.getStatement();

        if (state == Statement.LOBBY || state == Statement.EDITOR || state == Statement.QUEUE || state == Statement.SPECTATOR) {
            event.setCancelled(true);
        }

        if (loungePlayer.getParty() != null && state != Statement.PARTY_MATCH) {
            event.setCancelled(true);
        }


        if (state == Statement.FFA) {
            Location playerLocation = loungePlayer.getLocation();
            Location protectedLocation = LocationUtil.stringToLocation(Config.getString("spawn-locations.ffa"));
            int radius = 20;

            if (Bukkit.getWorld("ffa") != loungePlayer.getWorld()) return;
            if (playerLocation.distance(protectedLocation) > radius) return;

            event.setCancelled(true);
        }

        if (loungePlayer.getPartyMatch() != null) {
            loungePlayer.getPartyMatch().getTeam1().forEach(player -> {
                if (player == event.getDamager()) event.setCancelled(true);
            });

            if (loungePlayer.getPartyMatch().getTeam2() != null) {
                loungePlayer.getPartyMatch().getTeam2().forEach(player -> {
                    if (player == event.getDamager()) event.setCancelled(true);
                });
            }
        }
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of((Player) event.getEntity());
        Statement state = loungePlayer.getStatement();

        if (state == Statement.LOBBY || state == Statement.EDITOR || state == Statement.QUEUE || state == Statement.SPECTATOR) {
            event.setCancelled(true);
        }

        if (loungePlayer.getParty() != null && state != Statement.PARTY_MATCH) {
            event.setCancelled(true);
        }

        if (state == Statement.FFA) {
            Location playerLocation = loungePlayer.getLocation();
            Location editorLocation = LocationUtil.stringToLocation(Config.getString("spawn-locations.ffa"));
            int radius = 20;

            if (Bukkit.getWorld("ffa") != loungePlayer.getWorld()) return;
            if (playerLocation.distance(editorLocation) > radius) return;

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(event.getPlayer());
        Statement state = loungePlayer.getStatement();

        if (state == Statement.LOBBY || state == Statement.QUEUE || state == Statement.SPECTATOR) {
            event.setCancelled(true);
        }

        if (loungePlayer.getParty() != null && state != Statement.PARTY_MATCH) {
            event.setCancelled(true);
        }

        if (state == Statement.EDITOR) event.getItemDrop().remove();
    }

    @EventHandler
    public void on(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    // TODO: REMAKE FOR GROUP SYSTEM
    @EventHandler
    public void on(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    // TODO: REMAKE FOR GROUP SYSTEM
    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!event.getPlayer().isOp())
            event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent event) {
        LoungePlayer user = (LoungePlayer) LoungePlayer.of(event.getPlayer());

        if (user.getWorld() == Bukkit.getWorld("world")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(user);
                user.hidePlayer(player);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showPlayer(user);
                user.showPlayer(player);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getPlayer());

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) event.getClickedBlock().getState();
            if (sign.getLine(0).equals("[LEAVE EDITOR]")) {
                player.clearFullInventory();
                player.setStatement(Statement.LOBBY);
                LoungePractice.instance.getEditorSession().removeFromSession(player);
                player.sendToLobby();
            }
        }

        Block block = event.getClickedBlock();
        Location playerLocation = player.getLocation();
        Location editorLocation = LocationUtil.stringToLocation(Config.getString("spawn-locations.editor"));
        int radius = 10;

        if (!LoungePractice.instance.getEditorSession().getSessions().containsKey(player)) {
            return;
        }

        Kit kit = LoungePractice.instance.getEditorSession().getSessions().get(player);

        // Проверяем, что игрок находится в радиусе 10 блоков от установленной локации
        if (playerLocation.distance(editorLocation) > radius) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Проверяем, что игрок кликнул на сундук
            if (block.getType() == Material.CHEST) {
                Chest chest = (Chest) block.getState();

                // Открываем GUI
                new EditorExtraItemsUI(kit).showGUI(player);

                // Отменяем стандартное поведение сундука
                event.setCancelled(true);
            }
            // Проверяем, что игрок кликнул на наковальню
            else if (block.getType() == Material.ANVIL) {

                // Открываем GUI
                new EditorKitManagerUI(player, kit).showGUI();

                // Отменяем стандартное поведение наковальни
                event.setCancelled(true);
            }
        }
    }
}
