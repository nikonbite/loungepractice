package me.nikonbite.api.lib.command;


import me.nikonbite.api.lib.command.annotation.getter.CommandInfoGetter;

import java.util.List;

public abstract class CommandBase<T> implements CommandInfoGetter {

    public abstract void execute(T t, String... args);

    public abstract List<String> tabComplete(T t, String... args);
}
