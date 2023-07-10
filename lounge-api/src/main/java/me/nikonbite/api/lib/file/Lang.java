package me.nikonbite.api.lib.file;

import me.nikonbite.api.LoungeAPI;
import me.nikonbite.api.lib.util.ChatUtil;

import java.util.List;

public interface Lang {

    FileManager fileManager = LoungeAPI.getInstance().getFileManager();

    static String getString(String key, Object... args) {
        return ChatUtil.colorize(ChatUtil.format(fileManager.getLang().getString(key), args));
    }

    static int getInt(String key) {
        return fileManager.getLang().getInt(key);
    }

    static double getDouble(String key) {
        return fileManager.getLang().getDouble(key);
    }

    static List<String> getList(String key, Object... args) {
        return ChatUtil.colorize(ChatUtil.format(fileManager.getLang().getStringList(key), args));
    }
}