package me.nikonbite.api.lib.item.manager;

import me.nikonbite.api.lib.user.Gamer;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.api.lib.item.LoungeItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ItemManager implements Listener {

    public static final Set<LoungeItem> REGISTERED_ITEMS = new HashSet<>();

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        if (event.getItem() == null) return;

        Optional<LoungeItem> clickItem = REGISTERED_ITEMS.stream()
                .filter(clickItem1 -> clickItem1.getItem().isSimilar(event.getItem()))
                .findFirst();

        clickItem.ifPresent(loungeItem -> {
            if (loungeItem.getOnClick() == null) return;
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
                loungeItem.getOnClick().accept(event.getPlayer(), event.getClickedBlock());
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;

        Gamer player = Gamer.of((Player) event.getWhoClicked());

        if (player.getStatement() == Statement.LOBBY || player.getStatement() == Statement.PARTY) {
            event.setCancelled(true);
        }

        Optional<LoungeItem> clickItem = REGISTERED_ITEMS.stream()
                .filter(clickItem1 -> clickItem1.getItem().isSimilar(event.getCurrentItem()))
                .findFirst();

        clickItem.ifPresent(loungeItem -> {
        });
    }
}
