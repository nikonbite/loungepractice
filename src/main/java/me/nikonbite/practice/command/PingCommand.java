package me.nikonbite.practice.command;

import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import me.nikonbite.api.lib.util.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@CommandInfo(name = {"ping"})
public class PingCommand extends CommandBase<Player> {
    @Override
    public void execute(Player player, String... args) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);
        LoungePlayer target;

        switch (args.length) {
            default:
            case 0:
                loungePlayer.sendKeyMessage("messages.ping.self", "<ping>", loungePlayer.getPing());
                break;
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

                loungePlayer.sendKeyMessage("messages.ping.target", "<player>", target.getGroup().getColor() + target.getName(), "<ping>", target.getPing());
                loungePlayer.sendKeyMessage("messages.ping.difference",
                        "<difference>", NumberUtil.findDifference(target.getPing(), loungePlayer.getPing()));

                break;

        }
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        List<String> playerNames = new ArrayList<>();
        GamerRegistry.getUSER_MAP().forEach((integer, loungePlayer) -> playerNames.add(loungePlayer.getName()));
        return playerNames;
    }
}
