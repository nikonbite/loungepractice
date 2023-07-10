package me.nikonbite.practice.ui;

import me.nikonbite.practice.party.Party;
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

public class PartyListUI {
    private InventoryGUI gui;

    public PartyListUI() {
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(27)
                .contents(Lang.getString("ui.party.list.title"),
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

        Party.parties.forEach((loungePlayer, party) -> {
            contents.add(
                    GUIElementFactory.createActionItem(
                            new LoungeItem(Material.SKULL_ITEM)
                                    .withName(Lang.getString("ui.party.list.item-title", "<owner>", party.getOwner().getName(), "<count>", party.getPlayerList().size()))
                                    .withLore(Collections.emptyList())
                                    .build(),
                            event -> {
                                LoungePlayer user = (LoungePlayer) LoungePlayer.of(event.getViewer());

                                if (party.getOwner() == user) {
                                    return;
                                }
                            }
                    )
            );
        });

        return contents;
    }
}