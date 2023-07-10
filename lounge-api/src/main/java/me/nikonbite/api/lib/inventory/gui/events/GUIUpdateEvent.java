package me.nikonbite.api.lib.inventory.gui.events;

import me.nikonbite.api.lib.inventory.gui.session.GUISession;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Event that's fired when a player's GUI's contents is updated
 */
public class GUIUpdateEvent extends GUIEvent {
    private static final HandlerList handlers = new HandlerList(); //Required for Bukkit Events, can't just inherit
    private final GUISession session;
    private final Player viewer;

    public GUIUpdateEvent(GUISession session, Player viewer) {
        if (session == null || viewer == null) {
            throw new IllegalArgumentException();
        }
        this.session = session;
        this.viewer = viewer;
    }

    public static HandlerList getHandlerList() { //Required for Bukkit Events, can't just inherit
        return handlers;
    }

    /**
     * Get the GUISession associated with the updated GUI
     *
     * @return The GUISession
     */
    public GUISession getSession() {
        return session;
    }

    /**
     * Get the viewer who's GUI's contents was updated
     *
     * @return The viewer
     */
    public Player getViewer() {
        return viewer;
    }

    @Override
    public HandlerList getHandlers() { //Required for Bukkit Events, can't just inherit
        return handlers;
    }
}
