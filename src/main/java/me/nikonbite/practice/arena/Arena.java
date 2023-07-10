package me.nikonbite.practice.arena;

import lombok.Getter;
import me.nikonbite.api.lib.file.Arenas;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Arena {
    public static List<Arena> arenas = new ArrayList<>();
    private String name;
    private String firstPlayerSpawn;
    private String secondPlayerSpawn;
    private String spectatorSpawn;


    public Arena(String pathName) {
        try {
            String completePath = "arenas." + pathName;

            name = Arenas.getString(completePath + ".name");
            firstPlayerSpawn = Arenas.getString(completePath + ".firstPlayerSpawn");
            secondPlayerSpawn = Arenas.getString(completePath + ".secondPlayerSpawn");
            spectatorSpawn = Arenas.getString(completePath + ".spectatorSpawn");

            arenas.add(this);

            Bukkit.getServer().getLogger().info("LoungePractice | Loaded arena: " + name);
        } catch (Exception exc) {
            Bukkit.getServer().getLogger().info("LoungePractice | Invalid arena: " + name);
            exc.printStackTrace();
        }
    }

    public static void loadArenas() {
        ConfigurationSection configurationSection = Arenas.getConfigurationSection("arenas");
        configurationSection.getKeys(false).forEach(Arena::new);
    }
}
