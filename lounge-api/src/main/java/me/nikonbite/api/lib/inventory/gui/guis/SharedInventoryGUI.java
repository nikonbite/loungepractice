package me.nikonbite.api.lib.inventory.gui.guis;


import me.nikonbite.api.lib.inventory.gui.session.GUIState;

public interface SharedInventoryGUI extends InventoryGUI {
    /**
     * Get the shared GUIState that is shared by all viewers of this GUI
     *
     * @return The shared GUIState
     */
    GUIState getGUIState();

    /**
     * Recalculates the GUIElements to show the viewers (and what their display itemstacks are) and will update what the viewers see - if anybody is viewing this GUI.
     * If no players are viewing this GUI then this method will silently fail
     */
    void updateContentsAndView();

    /**
     * Will update what the viewers of the GUI see to match the GUI's state
     */
    void updateView();
}
