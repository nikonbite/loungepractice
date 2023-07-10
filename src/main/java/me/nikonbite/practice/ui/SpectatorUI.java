package me.nikonbite.practice.ui;

import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.practice.match.Match;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.item.LoungeItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

public class SpectatorUI {
    private InventoryGUI gui;

    public SpectatorUI() {
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(54)
                .contents(Lang.getString("ui.spectator.match-list.title"),
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

        for (Match match : Match.matchList) {
            contents.add(
                GUIElementFactory.createActionItem(
                        new LoungeItem(Material.PAPER)
                            .withName(Lang.getString("ui.spectator.match-list.item-title", "<player1>", match.getPlayer1().getName(), "<player2>", match.getPlayer2().getName()))
                            .withLore(Lang.getList("ui.spectator.match-list.item-lore", "<kit>", match.getKit().getName(), "<type>", match.getMatchType().getName()))
                            .build(),
                        player -> match.addSpectator((LoungePlayer) LoungePlayer.of(player)),
                        player -> match.addSpectator((LoungePlayer) LoungePlayer.of(player))
                )
            );
        }

        return contents;
    }
}
