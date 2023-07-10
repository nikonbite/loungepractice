package me.nikonbite.whitelist.util;

import net.md_5.bungee.api.ChatColor;

public class CC {
    public static String GRAY = ChatColor.GRAY.toString();
    public static String GREEN = ChatColor.DARK_GREEN.toString();
    public static String LIME = ChatColor.GREEN.toString();
    public static String PURPLE = ChatColor.LIGHT_PURPLE.toString();
    public static String RED = ChatColor.RED.toString();
    public static String WHITE = ChatColor.WHITE.toString();
    public static String YELLOW = ChatColor.YELLOW.toString();

    public static String BD_GREEN = ChatColor.DARK_GREEN + ChatColor.BOLD.toString();
    public static String BD_PURPLE = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString();

    public static String LINE = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString();

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}