package me.nikonbite.practice.command;

import me.nikonbite.practice.party.Party;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@CommandInfo(name = {"party", "p"})
public class PartyCommand extends CommandBase<Player> {

    @Override
    public void execute(Player player, String... args) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
        LoungePlayer target;
        Party party;

        switch (args.length) {
            default:
            case 0:
            case 1:
                loungePlayer.sendKeyList("messages.party.help");
                return;
            case 2:
                switch (args[0]) {
                    case "invite":
                        if (Party.parties.get(loungePlayer) == null) {
                            loungePlayer.sendKeyMessage("messages.party.party-need-to-create");
                            return;
                        }

                        if (Bukkit.getPlayerExact(args[1]) == null) {
                            loungePlayer.sendKeyMessage("messages.global.player-not-found");
                            return;
                        }

                        if (GamerRegistry.get(args[1]) == null) {
                            loungePlayer.sendKeyMessage("messages.global.player-not-found");
                            return;
                        }

                        target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[1]));
                        party = Party.parties.get(loungePlayer);

                        party.sendRequest(target);
                        return;
                    case "remove":
                        if (Party.parties.get(loungePlayer) == null) {
                            loungePlayer.sendKeyMessage("messages.party.party-need-to-create");
                            return;
                        }

                        if (Bukkit.getPlayerExact(args[1]) == null) {
                            loungePlayer.sendKeyMessage("messages.global.player-not-found");
                            return;
                        }

                        if (GamerRegistry.get(args[1]) == null) {
                            loungePlayer.sendKeyMessage("messages.global.player-not-found");
                            return;
                        }

                        target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[1]));
                        party = Party.parties.get(loungePlayer);

                        party.removeMember(target);
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

                        if (Party.parties.get(GamerRegistry.get(args[1])) == null) {
                            loungePlayer.sendKeyMessage("messages.party.party-unknown");
                            return;
                        }

                        target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[1]));
                        party = Party.parties.get(target);

                        if (!party.getRequests().contains(loungePlayer)) {
                            loungePlayer.sendKeyMessage("messages.party.player-no-requests");
                            return;
                        }

                        party.addMember(loungePlayer);
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

                        if (Party.parties.get(GamerRegistry.get(args[1])) == null) {
                            loungePlayer.sendKeyMessage("messages.party.party-unknown");
                            return;
                        }

                        target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[1]));
                        party = Party.parties.get(target);

                        if (!party.getRequests().contains(loungePlayer)) {
                            loungePlayer.sendKeyMessage("messages.party.player-no-requests");
                            return;
                        }

                        loungePlayer.sendKeyMessage("messages.party.player-declined-self", "<player>", target.getName());
                        target.sendKeyMessage("messages.party.player-declined", "<player>", loungePlayer.getName());
                        return;
                }
        }
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        if (args.length == 1) {
            return Arrays.asList("invite", "remove", "accept", "decline");
        } else {
            List<String> playerNames = new ArrayList<>();
            GamerRegistry.getUSER_MAP().forEach((integer, loungePlayer) -> playerNames.add(loungePlayer.getName()));
            return playerNames;
        }
    }
}
