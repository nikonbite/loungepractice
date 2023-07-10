package me.nikonbite.practice.ui;

import me.nikonbite.practice.match.type.MatchType;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.api.lib.item.LoungeItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyEventUI {
    private InventoryGUI gui;

    public PartyEventUI() {
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(9)
                .contents(Lang.getString("ui.party.event.title"),
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
                        3,
                        new LoungeItem(Material.LEASH)
                                .withName(Lang.getString("ui.party.event.split.item-title"))
                                .withLore(Collections.emptyList())
                                .build(),
                        event -> {
                            LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getViewer());
                            new KitSelectorUI(player, MatchType.PARTY_SPLIT).showGUI(player);
                        }
                )
        );

        contents.add(
                GUIElementFactory.createActionItem(
                        5,
                        new LoungeItem(Material.BLAZE_ROD)
                                .withName(Lang.getString("ui.party.event.ffa.item-title"))
                                .withLore(Collections.emptyList())
                                .build(),
                        event -> {
                            LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getViewer());
                            new KitSelectorUI(player, MatchType.PARTY_FFA).showGUI(player);
                        }
                )
        );

        return contents;
    }
}