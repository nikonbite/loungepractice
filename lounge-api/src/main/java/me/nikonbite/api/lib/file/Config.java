package me.nikonbite.api.lib.file;

import lombok.SneakyThrows;
import me.nikonbite.api.LoungeAPI;

import java.util.List;

public interface Config {

    FileManager fileManager = LoungeAPI.getInstance().getFileManager();

    static String getString(String path) {
        return fileManager.getConfig().getString(path);
    }

    static int getInt(String path) {
        return fileManager.getConfig().getInt(path);
    }

    static double getDouble(String path) {
        return fileManager.getConfig().getDouble(path);
    }

    static List<String> getList(String path) {
        return fileManager.getConfig().getStringList(path);
    }

    @SneakyThrows
    static void set(String path, Object value) {
        fileManager.getConfig().set(path, value);
        fileManager.getConfig().save(fileManager.getConfigFile());

    }
}
