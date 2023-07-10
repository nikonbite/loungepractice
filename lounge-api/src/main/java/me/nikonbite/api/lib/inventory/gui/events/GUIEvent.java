package me.nikonbite.api.lib.inventory.gui.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents an event that happens to a GUI.
 */
public class GUIEvent extends Event {
    private static final HandlerList handlers = new HandlerList(); //Required for Bukkit Events, can't just inherit

    public static HandlerList getHandlerList() { //Required for Bukkit Events, can't just inherit
        return handlers;
    }

    @Override
    public HandlerList getHandlers() { //Required for Bukkit Events, can't just inherit
        return handlers;
    }
}
