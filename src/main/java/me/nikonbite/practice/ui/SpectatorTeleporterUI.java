package me.nikonbite.practice.ui;

import me.nikonbite.practice.match.Match;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.api.lib.item.LoungeItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectatorTeleporterUI {
    private InventoryGUI gui;
    private final Match match;
    private final LoungePlayer spectator;

    public SpectatorTeleporterUI(LoungePlayer spectator, Match match) {
        this.spectator = spectator;
        this.match = match;
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(27)
                .contents(Lang.getString("ui.spectator.teleporter.title"),
                        genContents(),
                        true,
                        false,
                        false
                )
                .build();
    }

    public void showGUI() {
        gui.open(spectator);
    }

    protected List<GUIElement> genContents() {
        List<GUIElement> contents = new ArrayList<>();

        /// TODO MAKE THIS ALSO FOR PARTY MATCHES

        contents.add(
                GUIElementFactory.createActionItem(
                        new LoungeItem(new ItemStack(Material.SKULL_ITEM, 1, (short) 3))
                                .withName(Lang.getString("ui.spectator.teleporter.item-title", "<player>", match.getPlayer1().getName()))
                                .withLore(Collections.emptyList())
                                .build(),
                        event -> event.getViewer().teleport(match.getPlayer1().getLocation())
                )
        );

        contents.add(
                GUIElementFactory.createActionItem(
                        new LoungeItem(new ItemStack(Material.SKULL_ITEM, 1, (short) 3))
                                .withName(Lang.getString("ui.spectator.teleporter.item-title", "<player>", match.getPlayer2().getName()))
                                .withLore(Collections.emptyList())
                                .build(),
                        event -> event.getViewer().teleport(match.getPlayer2().getLocation())
                )
        );

        return contents;
    }
}