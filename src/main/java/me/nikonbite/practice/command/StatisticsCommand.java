package me.nikonbite.practice.command;

import me.nikonbite.practice.ui.StatisticsUI;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@CommandInfo(name = {"stats", "statistic", "stat", "statistics", "st"})
public class StatisticsCommand extends CommandBase<Player> {
    @Override
    public void execute(Player player, String... args) {
        new StatisticsUI((LoungePlayer) LoungePlayer.of(player)).showGUI();
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        return Collections.emptyList();
    }
}
