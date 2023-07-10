package me.nikonbite.api.lib.inventory.gui.events;

import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.session.GUISession;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Cancellable event that is fired when a GUIElement has been clicked on but has not yet handled the click
 */
public abstract class GUIClickEvent extends GUIEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList(); //Required for Bukkit Events, can't just inherit
    private final GUISession session;
    private final Player viewer;
    private final int inventorySlot;
    private final GUIElement interactedElement;
    private final InventoryClickEvent bukkitEvent;
    private boolean cancelled = false;

    public GUIClickEvent(GUISession session, Player viewer, int inventorySlot, GUIElement interactedElement, InventoryClickEvent bukkitEvent) {
        if (session == null || viewer == null || inventorySlot < 0 || bukkitEvent == null) {
            throw new IllegalArgumentException();
        }
        this.session = session;
        this.viewer = viewer;
        this.inventorySlot = inventorySlot;
        this.interactedElement = interactedElement;
        this.bukkitEvent = bukkitEvent;
    }

    public static HandlerList getHandlerList() { //Required for Bukkit Events, can't just inherit
        return handlers;
    }

    /**
     * Get the slot of the inventory interacted with
     *
     * @return The slot of the inventory interacted with
     */
    public int getInventorySlot() {
        return inventorySlot;
    }

    /**
     * Get the GUIElement interacted with, or null if none was
     *
     * @return The GUIElement interacted with, or null if none was
     */
    public GUIElement getInteractedElement() {
        return interactedElement;
    }

    /**
     * Get the Bukkit event that caused this event
     *
     * @return The Bukkit event that caused this event
     */
    public InventoryClickEvent getBukkitEvent() {
        return bukkitEvent;
    }

    /**
     * Get the GUISession associated with the click event
     *
     * @return The GUISession
     */
    public GUISession getSession() {
        return session;
    }

    /**
     * Get the viewer who just clicked
     *
     * @return The viewer
     */
    public Player getViewer() {
        return viewer;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() { //Required for Bukkit Events, can't just inherit
        return handlers;
    }
}
