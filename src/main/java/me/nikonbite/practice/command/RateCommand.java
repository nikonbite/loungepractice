package me.nikonbite.practice.command;

import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("unused")
@CommandInfo(name = {"rate", "ratemap"})
public class RateCommand extends CommandBase<Player> {
    @Override
    public void execute(Player player, String... args) {
        LoungePlayer.of(player).sendKeyMessage("messages.match.map-rated");
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        return null;
    }
}
