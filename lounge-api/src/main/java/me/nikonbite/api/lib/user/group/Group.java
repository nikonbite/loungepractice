package me.nikonbite.api.lib.user.group;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Group {

    DEFAULT("§a", "§aPlayer", "&a", 'G'),
    FAMOUS("§b✔§a", "&bFamous", "&a", 'F'),
    MEDIA("&7[&d&lMedia&7]&d", "&dMedia", "&d", 'E'),
    MODERATOR("&7[&2&lModerator&7]&2", "&2Moderator", "&2", 'D'),
    ADMINISTRATOR("&7[&c&lAdministrator&7]&c", "&cAdministrator", "&c", 'C'),
    MANAGER("&7[&9&lManager&7]&9", "&9Manager", "&9", 'B'),
    OWNER("&7[&4&lOwner&7]&4", "&4Owner", "&4", 'A')
    ;

    private final String prefix;
    private final String name;
    private final String color;
    private final char priority;

    private final static Group[] values = values();

    public static Group getByName(String name) {
        return Arrays.stream(values).filter(group -> group.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
