package me.nikonbite.practice.command;

import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.user.group.Group;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.command.annotation.CommandInfo;
import me.nikonbite.api.lib.file.Arenas;
import me.nikonbite.api.lib.file.Config;
import me.nikonbite.api.lib.util.LocationUtil;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@CommandInfo(name = {"setup"}, group = Group.OWNER)
public class SetupCommand extends CommandBase<Player> {
    @Override
    public void execute(Player player, String... args) {
        LoungePlayer user = (LoungePlayer) LoungePlayer.of(player);

        switch (args.length) {
            default:
            case 0:
               user.sendKeyList("setup.help");
               return;
            case 1:
                switch (args[0]) {
                    default:
                        user.sendKeyList("setup.help");
                        return;
                    case "lobby":
                    case "editor":
                    case "ffa":
                        Config.set("spawn-locations." + args[0], LocationUtil.locationToString(player.getLocation()));
                        user.sendKeyMessage("setup.location-set", "<location>", args[0]);
                        return;
                }
            case 2:
                switch (args[0]) {
                    case "arena":
                        Arenas.set("arenas." + args[1] + ".name", args[1]);

                        user.sendKeyMessage("setup.arena-created", "<arena>", args[1]);
                        return;

                    case "firstPlayerSpawn":
                    case "secondPlayerSpawn":
                    case "spectatorSpawn":
                        if (Arenas.getString("arenas." + args[1]) == null) {
                            user.sendKeyMessage("setup.arena-not-exist", "<arena>", args[1]);
                            return;
                        }

                        Arenas.set("arenas." + args[1] + "." + args[0], LocationUtil.locationToString(player.getLocation()));
                        user.sendKeyMessage("setup.location-set", "<location>", args[0]);
                        return;
                    default:
                        user.sendKeyList("setup.help");
                        return;
                }
        }
    }

    @Override
    public List<String> tabComplete(Player player, String... args) {
        if (args.length == 1) {
            return Arrays.asList("arena", "firstPlayerSpawn", "secondPlayerSpawn", "spectatorSpawn", "lobby", "editor", "ffa");
        } else {
            return Collections.emptyList();
        }
    }
}
