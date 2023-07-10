package me.nikonbite.api.lib.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public interface ChatUtil {

    static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    static String[] colorize(String[] string) {
        return Arrays.stream(string).map(s -> ChatColor.translateAlternateColorCodes('&', s)).toArray(String[]::new);
    }

    static List<String> colorize(List<String> strings) {
        return strings.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
    }

    static String format(String string, Object... format) {
        if (format.length == 0)
            return string;

        Map<String, Object> replace = new HashMap<>();
        AtomicReference<String> result = new AtomicReference<>(string);

        for (int j = 0; j < format.length; j++) {
            try {
                if ((j + 1) % 2 != 0) {
                    Object value = format[j + 1];
                    String key = (String) format[j];

                    replace.put(key, value);
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                break;
            }
        }

        replace.forEach((key, value) -> result.set(result.get().replace(key, value.toString())));

        return result.get();
    }

    static List<String> format(List<String> strings, Object... format) {
        if (format.length == 0) return strings;

        Map<String, Object> replace = new HashMap<>();
        List<String> result = new ArrayList<>(strings);

        for (int j = 0; j < format.length; j++) {
            try {
                if ((j + 1) % 2 != 0) {
                    Object value = format[j + 1];
                    String key = (String) format[j];

                    replace.put(key, value);
                }
            } catch (ArrayIndexOutOfBoundsException exception) {
                break;
            }
        }

        for (int i = 0; i < result.size(); i++) {
            final String[] str = {result.get(i)};
            replace.forEach((key, value) -> str[0] = str[0].replace(key, value.toString()));
            result.set(i, str[0]);
        }

        return result;
    }

    static TextComponent createClickableText(String text, String hoverText, String command) {
        TextComponent component = new TextComponent(text);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
        return component;
    }
}