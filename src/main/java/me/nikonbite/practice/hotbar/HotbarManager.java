package me.nikonbite.practice.hotbar;

import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.item.LoungeItem;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.match.Match;
import me.nikonbite.practice.match.type.MatchType;
import me.nikonbite.practice.party.Party;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.practice.ui.*;
import me.nikonbite.practice.user.LoungePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HotbarManager {

    /// Lobby
    public static ItemStack UNRANKED_ITEM =
            new LoungeItem(Material.IRON_SWORD, 1, (player, block) -> new KitSelectorUI(MatchType.UNRANKED).showGUI(player))
                    .withName(Lang.getString("items.unranked"))
                    .build();
    public static ItemStack RANKED_ITEM =
            new LoungeItem(Material.DIAMOND_SWORD, 1, (player, block) -> new KitSelectorUI(MatchType.RANKED).showGUI(player))
                    .withName(Lang.getString("items.ranked"))
                    .build();
    public static ItemStack FFA_ITEM =
            new LoungeItem(Material.GOLD_AXE, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
                loungePlayer.sendToFfa();
            })
                    .withName(Lang.getString("items.ffa"))
                    .build();
    public static ItemStack KIT_EDITOR_ITEM =
            new LoungeItem(Material.BOOK, 1, (player, block) -> new EditorUI().showGUI(player))
                    .withName(Lang.getString("items.kiteditor"))
                    .build();
    public static ItemStack SPECTATOR_ITEM =
            new LoungeItem(Material.REDSTONE_TORCH_ON, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
                loungePlayer.setStatement(Statement.SPECTATOR);
            })
                    .withName(Lang.getString("items.spectator"))
                    .build();
    public static ItemStack PARTY_CREATE_ITEM =
            new LoungeItem(Material.NAME_TAG, 1, (player, block) -> new Party((LoungePlayer) LoungePlayer.of(player)))
                    .withName(Lang.getString("items.party-create"))
                    .build();


    /// Queue
    public static ItemStack QUEUE_LEAVE_ITEM =
            new LoungeItem(Material.PAPER, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
                LoungePractice.instance.getQueueSystem().removePlayerFromQueue(loungePlayer);
            })
                    .withName(Lang.getString("items.queue-leave"))
                    .build();


    /// Spectator
    public static ItemStack SPECTATOR_TELEPORTER_ITEM =
            new LoungeItem(Material.COMPASS, 1, (player, block) -> {
                for (Match match : Match.matchList) {
                    LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);

                    if (match.getSpectators().contains(loungePlayer)) {
                        new SpectatorTeleporterUI(loungePlayer, match).showGUI();
                        return;
                    }

                    LoungePlayer.of(player).sendKeyMessage("messages.spectator.not-in-match");
                    return;
                }
            })
                    .withName(Lang.getString("items.spectator-teleport"))
                    .build();
    public static ItemStack SPECTATOR_LIST_ITEM =
            new LoungeItem(Material.PAPER, 1, (player, block) -> new SpectatorUI().showGUI(player))
                    .withName(Lang.getString("items.spectator-list"))
                    .build();
    public static ItemStack SPECTATOR_FFA_ITEM =
            new LoungeItem(Material.GOLD_AXE, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
                loungePlayer.sendToFfaAsSpectator();
            })
                    .withName(Lang.getString("items.spectator-ffa"))
                    .build();
    public static ItemStack SPECTATOR_LEAVE_ITEM =
            new LoungeItem(Material.REDSTONE_TORCH_ON, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
                loungePlayer.setStatement(Statement.LOBBY);
                loungePlayer.sendToLobby();
                Match.matchList.forEach(match -> {
                    if (match.getSpectators().contains(loungePlayer)) {
                        match.removeSpectator(loungePlayer);
                    }
                });
            })
                    .withName(Lang.getString("items.spectator-off"))
                    .build();


    /// Party
    public static ItemStack PARTY_LEAVE_ITEM =
            new LoungeItem(Material.FLINT_AND_STEEL, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);

                if (Party.parties.get(loungePlayer) != null && Party.parties.get(loungePlayer).getOwner() == loungePlayer) {
                    Party.parties.get(loungePlayer).delete();
                } else {
                    loungePlayer.getParty().memberLeave(loungePlayer);
                }
            })
                    .withName(Lang.getString("items.party-leave"))
                    .build();

    public static ItemStack PARTY_EVENTS_ITEM =
            new LoungeItem(Material.STONE_SWORD, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);

                if (loungePlayer.getParty().getOwner() != loungePlayer) {
                    loungePlayer.sendKeyMessage("messages.party.not-party-owner");
                    return;
                }

                if (loungePlayer.getParty().getPlayerList().size() < 2) {
                    loungePlayer.sendKeyMessage("messages.party.size-too-small");
                    return;
                }

                new PartyEventUI().showGUI(player);
            })
                    .withName(Lang.getString("items.party-events"))
                    .build();
    public static ItemStack PARTY_MEMBERS_ITEM =
            new LoungeItem(Material.GOLD_SWORD, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
                loungePlayer.getParty().sendMemberList(loungePlayer);
            })
                    .withName(Lang.getString("items.party-list-members"))
                    .build();
    public static ItemStack PARTY_FIGHT_OTHER_ITEM =
            new LoungeItem(Material.NETHER_STAR, 1, (player, block) -> {
                LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);

                if (loungePlayer.getParty().getOwner() != loungePlayer) {
                    loungePlayer.sendKeyMessage("messages.party.not-party-owner");
                    return;
                }

                new PartyListUI().showGUI(player);
            })
                    .withName(Lang.getString("items.party-fight-other-parties"))
                    .build();
}
