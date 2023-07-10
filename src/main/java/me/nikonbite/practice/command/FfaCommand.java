package me.nikonbite.practice.command;

import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@CommandInfo(name = {"ffa"})
public class FfaCommand extends CommandBase<Player> {

    @Override
    public void execute(Player player, String... args) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);

        switch (args.length) {
            default:
            case 0:
                loungePlayer.sendKeyList("messages.ffa.help");
                return;
            case 1:
                switch (args[0]) {
                    default:
                        loungePlayer.sendKeyList("messages.ffa.help");
                        return;
                    case "join":
                        loungePlayer.sendToFfa();
                        return;
                    case "leave":
                        loungePlayer.removeFromFfa();
                        return;
                }
        }
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        if (args.length == 1) {
            return Arrays.asList("join", "leave");
        } else {
            return Collections.emptyList();
        }
    }
}
