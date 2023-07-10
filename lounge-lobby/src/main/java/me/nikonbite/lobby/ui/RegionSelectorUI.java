package me.nikonbite.lobby.ui;

import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.api.lib.item.LoungeItem;
import me.nikonbite.api.lib.user.Gamer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

public class RegionSelectorUI {
    private InventoryGUI gui;

    public RegionSelectorUI() {
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(3 * 9)
                .contents(Lang.getString("ui.region-selector.title"),
                        genContents(),
                        true,
                        false,
                        false
                )
                .build();
    }

    public void showGUI(Player player) {
        gui.open(player);
    }

    protected List<GUIElement> genContents() {
        List<GUIElement> contents = new ArrayList<>();

        contents.add(
                GUIElementFactory.createActionItem(
                        new LoungeItem(Material.DIAMOND_SWORD)
                                .withName(Lang.getString("ui.region-selector.region.item-title", "<region>", "EU"))
                                .withLore(Lang.getList("ui.region-selector.region.item-lore", "<online>", "0"))
                                .build(),
                        player -> Gamer.of(player.getViewer()).sendToServer("us-practice-1")
                )
        );

        contents.add(
                GUIElementFactory.createActionItem(
                        new LoungeItem(Material.DIAMOND_SWORD)
                                .withName(Lang.getString("ui.region-selector.region.item-title", "<region>", "US"))
                                .withLore(Lang.getList("ui.region-selector.region.item-lore", "<online>", "0"))
                                .build(),
                        player -> Gamer.of(player.getViewer()).sendToServer("us-practice-1")
                )
        );

        contents.add(
                GUIElementFactory.createActionItem(
                        new LoungeItem(Material.DIAMOND_SWORD)
                                .withName(Lang.getString("ui.region-selector.quit.item-title"))
                                .build(),
                        player -> player.getViewer().kickPlayer(Lang.getString("messages.global.quit"))
                )
        );

        return contents;
    }
}
