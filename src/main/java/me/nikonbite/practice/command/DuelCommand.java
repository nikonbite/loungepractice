package me.nikonbite.practice.command;

import me.nikonbite.practice.match.duel.DuelSystem;
import me.nikonbite.practice.match.type.MatchType;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.practice.ui.KitSelectorUI;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = {"duel"})
public class DuelCommand extends CommandBase<Player> {

    @Override
    public void execute(Player player, String... args) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
        LoungePlayer target;
        List<DuelSystem> duelSystemListCopy;

        switch (args.length) {
            default:
            case 0:
                loungePlayer.sendKeyList("messages.duel.help");
                return;
            case 1:
                if (Bukkit.getPlayerExact(args[0]) == null) {
                    loungePlayer.sendKeyMessage("messages.global.player-not-found");
                    return;
                }

                if (GamerRegistry.get(args[0]) == null) {
                    loungePlayer.sendKeyMessage("messages.global.player-not-found");
                    return;
                }

                target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[0]));

                if (target == loungePlayer) {
                    loungePlayer.sendKeyMessage("messages.duel.cannot-yourself");
                    return;
                }

                if (target.getStatement() != Statement.LOBBY) {
                    loungePlayer.sendKeyMessage("messages.duel.player-busy", "<player>", target.getName());
                    return;
                }

                new KitSelectorUI(target, MatchType.DUEL).showGUI(loungePlayer);
                return;
            case 2:
                switch (args[0]) {
                    default:
                        loungePlayer.sendKeyList("messages.duel.help");
                        return;
                    case "accept":
                        if (Bukkit.getPlayerExact(args[1]) == null) {
                            loungePlayer.sendKeyMessage("messages.global.player-not-found");
                            return;
                        }

                        if (GamerRegistry.get(args[1]) == null) {
                            loungePlayer.sendKeyMessage("messages.global.player-not-found");
                            return;
                        }

                        target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[1]));

                        if (target.getStatement() != Statement.LOBBY) {
                            loungePlayer.sendKeyMessage("messages.duel.player-busy", "<player>", target.getName());
                            return;
                        }

                        duelSystemListCopy = new ArrayList<>(DuelSystem.duelSystemList);
                        duelSystemListCopy.forEach(duelSystem -> {
                            if (duelSystem.getTarget() == loungePlayer) {
                                duelSystem.acceptDuelRequest(target);
                                return;
                            }
                        });
                        return;
                    case "decline":
                        if (Bukkit.getPlayerExact(args[1]) == null) {
                            loungePlayer.sendKeyMessage("messages.global.player-not-found");
                            return;
                        }

                        if (GamerRegistry.get(args[1]) == null) {
                            loungePlayer.sendKeyMessage("messages.global.player-not-found");
                            return;
                        }

                        target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[1]));

                        duelSystemListCopy = new ArrayList<>(DuelSystem.duelSystemList);

                        duelSystemListCopy.forEach(duelSystem -> {
                            if (duelSystem.getTarget() == loungePlayer)
                                duelSystem.declineDuelRequest(target);
                        });
                        return;
                }
        }
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        if (args.length == 1) {
            List<String> playerNames = new ArrayList<>();
            GamerRegistry.getUSER_MAP().forEach((integer, loungePlayer) -> playerNames.add(loungePlayer.getName()));
            return playerNames;
        } else {
            return Collections.emptyList();
        }
    }
}
