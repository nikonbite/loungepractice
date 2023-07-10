package me.nikonbite.practice.ui;

import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.match.Match;
import me.nikonbite.practice.match.type.MatchType;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.item.LoungeItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class KitSelectorUI {
    private InventoryGUI gui;
    private LoungePlayer target;
    private final MatchType matchType;

    public KitSelectorUI(LoungePlayer target, MatchType matchType) {
        this.target = target;
        this.matchType = matchType;
        createGUI();
    }

    public KitSelectorUI(MatchType matchType) {
        this.matchType = matchType;
        createGUI();
    }

    protected void createGUI() {
        if (matchType == MatchType.DUEL || matchType == MatchType.PARTY_FFA || matchType == MatchType.PARTY_SPLIT || matchType == MatchType.PARTY_VS_PARTY) {
            gui = new GUIBuilder()
                    .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                    .inventoryType(InventoryType.CHEST)
                    .dynamicallyResizeToWrapContent(false)
                    .size(27)
                    .contents(Lang.getString("ui.duel.kit-selector.title"),
                            genContents(),
                            false,
                            false,
                            false
                    )
                    .build();
        } else  {
            gui = new GUIBuilder()
                    .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                    .inventoryType(InventoryType.CHEST)
                    .dynamicallyResizeToWrapContent(false)
                    .size(27)
                    .contents(Lang.getString("ui.selector.title", "<type>", matchType.getName()),
                            genContents(),
                            false,
                            false,
                            false
                    )
                    .build();
        }
    }

    public void showGUI(Player player) {
        gui.open(player);
    }

    protected List<GUIElement> genContents() {
        List<GUIElement> contents = new ArrayList<>();

        for (Kit kit : Kit.values()) {
            if (matchType == MatchType.DUEL || matchType == MatchType.PARTY_FFA || matchType == MatchType.PARTY_SPLIT || matchType == MatchType.PARTY_VS_PARTY) {
                contents.add(
                        GUIElementFactory.createClickableItem(
                                new LoungeItem(kit.getIcon())
                                        .withName(Lang.getString("ui.duel.kit-selector.item-title", "<kit-name>", kit.getName()))
                                        .withLore(Collections.emptyList())
                                        .build(),
                                player -> new ArenaSelectorUI((LoungePlayer) LoungePlayer.of(target), kit, matchType).showGUI(player),
                                kit.ordinal()
                        )
                );
            } else {
                contents.add(
                        GUIElementFactory.createClickableItem(
                                new LoungeItem(kit.getIcon())
                                        .withName(Lang.getString("ui.selector.item-title", "<kit-name>", kit.getName()))
                                        .withLore(Lang.getList("ui.selector.item-lore",
                                                "<queue-online>", LoungePractice.instance.getQueueSystem().getPlayersCountInQueue(kit, matchType),
                                                "<match-online>", Match.getPlayersCountInMatch(kit, matchType)))
                                        .build(),
                                player -> new ArenaSelectorUI(kit, matchType).showGUI(player),
                                kit.ordinal()
                        )
                );
            }
        }

        return contents;
    }
}