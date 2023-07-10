package me.nikonbite.api.lib.user.registry;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import me.nikonbite.api.LoungeAPI;
import me.nikonbite.api.lib.user.Gamer;
import me.nikonbite.api.lib.user.group.Group;
import me.nikonbite.api.lib.user.wrapper.DataWrapper;
import org.bukkit.entity.Player;

import java.util.Map;

@UtilityClass
public class GamerRegistry {

    @Getter
    private static final Map<Integer, Gamer> USER_MAP = Maps.newHashMap();

    public Gamer get(int id) {
        return USER_MAP.computeIfAbsent(id, Gamer::new);
    }

    public Gamer get(Player player) {
        return get(player.getName());
    }

    public Gamer get(String name) {
        return get(DataWrapper.getId(name));
    }

    public static int insertId(String name) {
        LoungeAPI.hikariConnection.execute("INSERT INTO `Identifiers` (`Name`) VALUES (?)", name);

        return DataWrapper.getId(name);
    }

    public static Group insertGroup(Gamer user) {
        Group group = Group.DEFAULT;

        LoungeAPI.hikariConnection.execute("INSERT INTO `Groups` VALUES(?, ?)", user.getId(), group.name());

        return group;
    }
}