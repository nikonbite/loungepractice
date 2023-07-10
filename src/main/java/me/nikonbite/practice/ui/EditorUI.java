package me.nikonbite.practice.ui;

import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.api.lib.item.LoungeItem;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditorUI {
    private InventoryGUI gui;

    public EditorUI() {
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(27)
                .contents(Lang.getString("ui.editor.kit-selector.title"),
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

        for (Kit kit : Kit.values()) {
            contents.add(
                GUIElementFactory.createActionItem(
                        new LoungeItem(kit.getIcon())
                            .withName(Lang.getString("ui.editor.kit-selector.item-title", "<kit-name>", kit.getName()))
                            .withLore(Collections.emptyList())
                            .build(),
                        event -> {
                            LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(event.getViewer());
                            loungePlayer.sendToEditor(kit);
                        }
                )
            );
        }

        return contents;
    }
}