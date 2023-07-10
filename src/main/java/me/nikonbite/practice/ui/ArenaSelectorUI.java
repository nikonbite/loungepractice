package me.nikonbite.practice.ui;

import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.arena.Arena;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.match.PartyMatch;
import me.nikonbite.practice.match.duel.DuelSystem;
import me.nikonbite.practice.match.type.MatchType;
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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ArenaSelectorUI {
    private InventoryGUI gui;
    private final List<Arena> selectedArenas;
    private LoungePlayer target;
    private final Kit kit;
    private final MatchType matchType;

    public ArenaSelectorUI(LoungePlayer target, Kit kit, MatchType matchType) {
        this.target = target;
        this.kit = kit;
        this.matchType = matchType;
        selectedArenas = new ArrayList<>();
        createGUI();
    }

    public ArenaSelectorUI(Kit kit, MatchType matchType) {
        this.kit = kit;
        this.matchType = matchType;
        selectedArenas = new ArrayList<>();
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(27)
                .contents(Lang.getString("ui.arena-selector.title"),
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

        for (Arena arena : Arena.arenas) {
            contents.add(
                    GUIElementFactory.createActionItem(
                            Arena.arenas.indexOf(arena),
                            new LoungeItem(new ItemStack(Material.INK_SACK, 1, (short) 1))
                                    .withName(Lang.getString("ui.arena-selector.arena.item-title", "<arena-name>", arena.getName()))
                                    .withLore(Collections.emptyList())
                                    .build(),
                            event -> {
                                if (selectedArenas.contains(arena)) selectedArenas.remove(arena);
                                else selectedArenas.add(arena);
                                event.getBukkitEvent().getCurrentItem().setDurability(selectedArenas.contains(arena) ? (short) 10 : (short) 1);
                            }
                    )
            );
        }

        contents.add(
                GUIElementFactory.createActionItem(
                        26,
                        new LoungeItem(Material.PAPER)
                                .withName(Lang.getString("ui.arena-selector.select.item-title"))
                                .withLore(Collections.emptyList())
                                .build(),
                        event -> {
                            if (selectedArenas.isEmpty()) selectedArenas.addAll(Arena.arenas);

                            Party party;

                            switch (matchType) {
                                case UNRANKED:
                                case RANKED:
                                    addToQueue((LoungePlayer) LoungePlayer.of(event.getViewer()), kit, matchType, selectedArenas);
                                    event.getViewer().closeInventory();
                                    return;
                                case DUEL:
                                    new DuelSystem((LoungePlayer) LoungePlayer.of(event.getViewer()), target, kit, selectedArenas).sendDuelRequest();
                                    event.getViewer().closeInventory();
                                    return;
                                case PARTY_FFA:
                                    party = target.getParty();
                                    new PartyMatch(party.getPlayerList(), matchType, kit, selectedArenas.get(new Random().nextInt(selectedArenas.size()))).start();
                                    return;
                                case PARTY_SPLIT:
                                    party = target.getParty();

                                    List<LoungePlayer> notReadyTeam = party.getPlayerList();

                                    int mid = notReadyTeam.size() / 2;
                                    List<LoungePlayer> team1 = notReadyTeam.subList(0, mid);
                                    List<LoungePlayer> team2 = notReadyTeam.subList(mid, notReadyTeam.size());

                                    new PartyMatch(team1, team2, matchType, kit, selectedArenas.get(new Random().nextInt(selectedArenas.size()))).start();
                                    return;
                                case PARTY_VS_PARTY:
                                    return;
                            }
                        }

                )
        );

        return contents;
    }

    public void addToQueue(LoungePlayer loungePlayer, Kit kit, MatchType matchType, List<Arena> arenas) {
        LoungePractice.instance.getQueueSystem().addPlayerToQueue(loungePlayer, kit, matchType, arenas);
        loungePlayer.closeInventory();
    }
}
