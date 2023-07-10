package me.nikonbite.api.lib.command.manager;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.nikonbite.api.lib.command.CommandBase;
import me.nikonbite.api.lib.user.Gamer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@UtilityClass
public class CommandManager {

    @Getter
    private static CommandMap commandMap;

    /**
     * Метод для регистрации команды.
     * */
    @SuppressWarnings("unchecked")
    public <T> void registerCommand(CommandBase<T> commandBase) {
        commandMap.register("LoungePractice", new Command(commandBase.getName(), "", "", commandBase.getAliases()) {
            @Override
            public boolean execute(@NonNull CommandSender sender, @NonNull String s, @NonNull String[] strings) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only for players!");
                    return true;
                }

                if (Gamer.of((Player) sender).getGroup().ordinal() < commandBase.getGroup().ordinal()) {
                    Gamer.of((Player) sender).sendKeyMessage("messages.global.no-perm");
                    return true;
                }

                commandBase.execute((T) sender, strings);
                return true;
            }

            @Override
            public List<String> tabComplete(@NonNull CommandSender sender, @NonNull String alias, @NonNull String[] args) throws IllegalArgumentException {
                if (!(sender instanceof Player)) {
                    return Collections.emptyList();
                }

                return commandBase.tabComplete((T) sender, args);
            }
        });
    }

    public static void registerCommands(Plugin plugin) {
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());
        Set<Class<? extends CommandBase>> commandClasses = reflections.getSubTypesOf(CommandBase.class);

        for (Class<? extends CommandBase> commandClass : commandClasses) {
            try {
                CommandBase<Player> commandBase = commandClass.getDeclaredConstructor().newInstance();
                registerCommand(commandBase);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void registerCommands(String commandsPackage) {
        Reflections reflections = new Reflections(commandsPackage);
        Set<Class<? extends CommandBase>> commandClasses = reflections.getSubTypesOf(CommandBase.class);

        for (Class<? extends CommandBase> commandClass : commandClasses) {
            try {
                CommandBase<Player> commandBase = commandClass.getDeclaredConstructor().newInstance();
                registerCommand(commandBase);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    static {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());

            commandMapField.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}