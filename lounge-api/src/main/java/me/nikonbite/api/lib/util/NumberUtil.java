package me.nikonbite.api.lib.util;

public interface NumberUtil {

    static String findDifference(int a, int b) {
        int diff = a - b;
        String sign = diff >= 0 ? "+" : "-";
        String prefix = diff >= 0 ? "&a" : "&c";

        String result = sign + Math.abs(diff);
        result = prefix + result;

        return ChatUtil.colorize(result);
    }
}
