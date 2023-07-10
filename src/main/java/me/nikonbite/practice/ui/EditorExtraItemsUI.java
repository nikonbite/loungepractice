package me.nikonbite.practice.ui;

import me.nikonbite.practice.kit.Kit;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EditorExtraItemsUI {
    private InventoryGUI gui;
    private final Kit kit;

    public EditorExtraItemsUI(Kit kit) {
        this.kit = kit;
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(27)
                .contents(Lang.getString("ui.editor.extra-items.title", "<kit>", kit.getName()),
                        genContents(),
                        true,
                        false,
                        false
                )
                .build();
    }

    public void showGUI(Player player) {
        gui.open(player);
    }

    protected List<GUIElement> genContents() {
        List<GUIElement> contents = new ArrayList<>();

        for (ItemStack item : kit.getExtras().values()) {
            contents.add(
                GUIElementFactory.createActionItem(
                        item,
                        event -> {
                            event.getViewer().setItemOnCursor(item);
                            event.getSession().getInventoryGUI().updateContentsAndView(event.getViewer());
                        }
                )
            );
        }

        return contents;
    }
}
