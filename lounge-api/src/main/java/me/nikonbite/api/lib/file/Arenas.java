package me.nikonbite.api.lib.file;

import lombok.SneakyThrows;
import me.nikonbite.api.LoungeAPI;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public interface Arenas {
    FileManager fileManager = LoungeAPI.getInstance().getFileManager();

    static String getString(String path) {
        return fileManager.getArenas().getString(path);
    }

    static int getInt(String path) {
        return fileManager.getArenas().getInt(path);
    }

    static double getDouble(String path) {
        return fileManager.getArenas().getDouble(path);
    }

    static List<String> getList(String path) {
        return fileManager.getArenas().getStringList(path);
    }

    static ConfigurationSection getConfigurationSection(String path) {
        return fileManager.getArenas().getConfigurationSection(path);
    }

    @SneakyThrows
    static void set(String path, Object value) {
        fileManager.getArenas().set(path, value);
        fileManager.getArenas().save(fileManager.getArenasFile());
    }
}
