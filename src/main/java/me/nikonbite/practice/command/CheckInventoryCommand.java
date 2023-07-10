package me.nikonbite.practice.command;

import me.nikonbite.practice.listener.MatchListener;
import me.nikonbite.practice.ui.PlayerInventoryUI;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@CommandInfo(name = {"checksavedinv"})
public class CheckInventoryCommand extends CommandBase<Player> {

    @Override
    public void execute(Player player, String[] args) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);

        if (Bukkit.getPlayerExact(args[0]) == null) {
            loungePlayer.sendKeyMessage("messages.global.player-not-found");
            return;
        }

        if (GamerRegistry.get(args[0]) == null) {
            loungePlayer.sendKeyMessage("messages.global.player-not-found");
            return;
        }

        LoungePlayer target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[0]));

        if (MatchListener.savedPlayersInfo.get(target) == null) {
            System.out.println("== null");
            System.out.println(MatchListener.savedPlayersInfo.get(target));
            return;
        }

        new PlayerInventoryUI(MatchListener.savedPlayersInfo.get(target)).showGUI(player);
        return;
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        return Collections.emptyList();
    }
}
