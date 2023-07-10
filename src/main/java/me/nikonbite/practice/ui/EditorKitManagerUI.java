package me.nikonbite.practice.ui;

import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.api.lib.item.LoungeItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

public class EditorKitManagerUI {
    private InventoryGUI gui;
    private final LoungePlayer loungePlayer;
    private final Kit kit;

    public EditorKitManagerUI(LoungePlayer loungePlayer, Kit kit) {
        this.kit = kit;
        this.loungePlayer = loungePlayer;
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(27)
                .contents(Lang.getString("ui.editor.kit-manager.title", "<kit>", kit.getName()),
                        genContents(),
                        true,
                        false,
                        false
                )
                .build();
    }

    public void showGUI() {
        gui.open(loungePlayer);
    }

    protected List<GUIElement> genContents() {
        List<GUIElement> contents = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            int finalI = i;
            contents.add(
                    GUIElementFactory.createActionItem(
                            i + 1,
                            new LoungeItem(Material.CHEST)
                                    .withName(Lang.getString("ui.editor.kit-manager.save.item-title", "<custom-kit-name>", kit.getName() + " -#" + (i + 1)))
                                    .build(),
                            event -> {
                                LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getViewer());

                                if (finalI < player.getKitLoadout().getCustomKit(kit).size()) {
                                    player.getKitLoadout().getCustomKit(kit).set(finalI, event.getViewer().getInventory().getContents());
                                } else {
                                    player.getKitLoadout().getCustomKit(kit).add(event.getViewer().getInventory().getContents());
                                }

                                updateContent();
                            }
                    )
            );

            if (finalI < loungePlayer.getKitLoadout().getCustomKit(kit).size() && loungePlayer.getKitLoadout().getCustomKit(kit).get(finalI) != null) {
                contents.add(
                        GUIElementFactory.createActionItem(
                                i + 10,
                                new LoungeItem(Material.BOOK)
                                        .withName(Lang.getString("ui.editor.kit-manager.load.item-title", "<custom-kit-name>", kit.getName() + "-#" + (i + 1)))
                                        .build(),
                                event -> {
                                    LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getViewer());
                                    if (player != null) {
                                        event.getViewer().getInventory().clear();
                                        event.getViewer().getInventory().setContents(player.getKitLoadout().getCustomKitById(kit, finalI));
                                        player.closeInventory();
                                    }
                                }
                        )
                );
                contents.add(
                        GUIElementFactory.createActionItem(
                                i + 19,
                                new LoungeItem(Material.FLINT).withName(Lang.getString("ui.editor.kit-manager.delete.item-title", "<custom-kit-name>", kit.getName() + "-#" + (i + 1))).build(),
                                event -> {
                                    LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getViewer());
                                    if (player != null) {
                                        player.getKitLoadout().getCustomKit(kit).remove(finalI);
                                        updateContent();
                                    }
                                }
                        )
                );
            }
        }

        return contents;
    }

    private void updateContent() {
        new EditorKitManagerUI(loungePlayer, kit).showGUI();
    }
}