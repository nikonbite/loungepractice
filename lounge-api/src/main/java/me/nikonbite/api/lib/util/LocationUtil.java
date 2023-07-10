package me.nikonbite.api.lib.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public interface LocationUtil {

    /**
     * Преобразует Location в строку.
     *
     * @param location Объект Location, который нужно преобразовать.
     * @return Строку, представляющую Location.
     */
    static String locationToString(Location location) {
        if (location == null) return null;

        return String.format("%s, %s, %s, %s, %s, %s", location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * Преобразует строку в Location.
     *
     * @param locString Строка, которую нужно преобразовать.
     * @return Новый объект Location или null, если преобразование не удалось.
     */
    static Location stringToLocation(String locString) {
        if (locString == null) return null;

        String[] locData = locString.split(", ");

        return new Location(Bukkit.getWorld(locData[0]), Double.parseDouble(locData[1]), Double.parseDouble(locData[2]), Double.parseDouble(locData[3]),
                Float.parseFloat(locData[4]), Float.parseFloat(locData[5]));
    }
}
