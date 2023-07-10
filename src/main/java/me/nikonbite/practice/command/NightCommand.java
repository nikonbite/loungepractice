package me.nikonbite.practice.command;

import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(name = {"night", "n"})
public class NightCommand extends CommandBase<Player> {


    @Override
    public void execute(Player player, String... args) {
        player.setPlayerTime(13000, false);
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        return null;
    }
}