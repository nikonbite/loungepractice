package me.nikonbite.practice.command;

import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.user.group.Group;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import me.nikonbite.api.lib.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(name = {"group", "rank"}, group = Group.OWNER)
public class GroupCommand extends CommandBase<Player> {

    @Override
    public void execute(Player player, String... args) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player);

        switch (args.length) {
            default:
            case 0:
            case 2:
                loungePlayer.sendKeyList("admin.group.help");
                return;
            case 1:
                if (args[0].equals("list")) {
                    loungePlayer.sendMessage(ChatUtil.colorize("&eGroup list:"));
                    for (Group group : Group.values())
                        loungePlayer.sendMessage(ChatUtil.colorize(
                                " &7- " + group.getName() + " | " + group.getColor() + group.name()));
                    return;
                }
                loungePlayer.sendKeyList("admin.group.help");
                return;
            case 3:
                if (args[0].equals("set")) {
                    if (Bukkit.getPlayerExact(args[1]) == null) {
                        loungePlayer.sendKeyMessage("messages.global.player-not-found");
                        return;
                    }

                    if (GamerRegistry.get(args[1]) == null) {
                        loungePlayer.sendKeyMessage("messages.global.player-not-found");
                        return;
                    }

                    LoungePlayer target = (LoungePlayer) LoungePlayer.of(GamerRegistry.get(args[1]));

                    if (Group.getByName(args[2]) == null) {
                        loungePlayer.sendKeyMessage("admin.group.not-found", "<group>", args[2]);
                        return;
                    }

                    if (target.getGroup() == Group.getByName(args[2])) {
                        loungePlayer.sendKeyMessage("admin.group.already-exist", "<group>", Group.getByName(args[2]).getName());
                        return;
                    }

                    target.setGroup(Group.getByName(args[2]));
                    loungePlayer.sendKeyMessage("admin.group.success-setted",
                            "<player>", target.getName(), "<group>", Group.getByName(args[2]).getName());
                    target.sendKeyMessage("admin.group.success-setted-self",
                            "<player>", loungePlayer.getName(), "<group>", Group.getByName(args[2]).getName());
                    return;
                }
                loungePlayer.sendKeyList("admin.group.help");
                return;

        }
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        if (args.length == 1) {
            return Arrays.asList("set", "list");
        } else {
            List<String> playerNames = new ArrayList<>();
            GamerRegistry.getUSER_MAP().forEach((integer, loungePlayer) -> playerNames.add(loungePlayer.getName()));
            return playerNames;
        }
    }
}
