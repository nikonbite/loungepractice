package me.nikonbite.api.lib.inventory.gui.events;

import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.session.GUISession;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Cancellable event that is fired when a GUIElement has been an item placed in it but has not yet handled it
 */
public class GUIPlaceItemEvent extends GUIClickEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList(); //Required for Bukkit Events, can't just inherit

    public GUIPlaceItemEvent(GUISession session, Player viewer, int inventorySlot, GUIElement interactedElement, InventoryClickEvent bukkitEvent) {
        super(session, viewer, inventorySlot, interactedElement, bukkitEvent);
    }

    public static HandlerList getHandlerList() { //Required for Bukkit Events, can't just inherit
        return handlers;
    }

    @Override
    public HandlerList getHandlers() { //Required for Bukkit Events, can't just inherit
        return handlers;
    }
}