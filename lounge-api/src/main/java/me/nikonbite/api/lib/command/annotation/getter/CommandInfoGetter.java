package me.nikonbite.api.lib.command.annotation.getter;

import me.nikonbite.api.lib.command.annotation.CommandInfo;
import me.nikonbite.api.lib.user.group.Group;

import java.util.Arrays;
import java.util.List;

public interface CommandInfoGetter {

    default String getName() {
        return getClass().getAnnotation(CommandInfo.class).name()[0];
    }

    default List<String> getAliases() {
        return Arrays.asList(getClass().getAnnotation(CommandInfo.class).name());
    }

    default Group getGroup() {
        return getClass().getAnnotation(CommandInfo.class).group();
    }

}
