package me.nikonbite.api.lib.file;

import lombok.Getter;
import me.nikonbite.api.starter.bukkit.BukkitAPIStarter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileManager {

    @Getter
    public static File MAIN_DIR = new File("./lounge-files/");

    @Getter
    private FileConfiguration lang;
    @Getter
    private File langFile;

    @Getter
    private FileConfiguration arenas;
    @Getter
    private File arenasFile;

    @Getter
    private FileConfiguration config;
    @Getter
    private File configFile;

    public void registerFiles() {
        langFile = new File(MAIN_DIR, "lang.yml");
        if (!langFile.exists()) {
            BukkitAPIStarter.getInstance().saveResource("lang.yml", false);
        }
        lang = YamlConfiguration.loadConfiguration(langFile);

        configFile = new File(MAIN_DIR, "config.yml");
        if (!configFile.exists()) {
            BukkitAPIStarter.getInstance().saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        arenasFile = new File(MAIN_DIR, "arenas.yml");
        if (!arenasFile.exists()) {
            BukkitAPIStarter.getInstance().saveResource("arenas.yml", false);
        }
        arenas = YamlConfiguration.loadConfiguration(arenasFile);
    }
}