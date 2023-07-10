package me.nikonbite.api.lib.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public interface RegistrationUtil {

    static void registerListeners(Plugin plugin) {
        Reflections reflections = new Reflections(plugin.getClass().getPackage().getName());
        Set<Class<? extends Listener>> listenerClasses = reflections.getSubTypesOf(Listener.class);

        for (Class<? extends Listener> listenerClass : listenerClasses) {
            try {
                Listener listener = listenerClass.getDeclaredConstructor().newInstance();
                Bukkit.getPluginManager().registerEvents(listener, plugin);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    static void registerListeners(Plugin plugin, String listenersPackage) {
        Reflections reflections = new Reflections(listenersPackage);
        Set<Class<? extends Listener>> listenerClasses = reflections.getSubTypesOf(Listener.class);

        for (Class<? extends Listener> listenerClass : listenerClasses) {
            try {
                Listener listener = listenerClass.getDeclaredConstructor().newInstance();
                Bukkit.getPluginManager().registerEvents(listener, plugin);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
