package me.nikonbite.practice.ui;

import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.api.lib.item.LoungeItem;
import me.nikonbite.api.lib.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PlayerInventoryUI {
    private InventoryGUI gui;
    private final Map<String, Object> savedPlayersInfo;

    public PlayerInventoryUI(Map<String, Object> savedPlayersInfo) {
        this.savedPlayersInfo = savedPlayersInfo;
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(54)
                .contents(Lang.getString("ui.player-inventory.title", "<player>", savedPlayersInfo.get("playerName")),
                        genContents(),
                        false,
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


        ItemStack[] notReadyInventory = (ItemStack[]) savedPlayersInfo.get("inventory");
        ItemStack[] inventory = new ItemStack[notReadyInventory.length];

        System.arraycopy(notReadyInventory, 9, inventory, 0, notReadyInventory.length - 9);
        System.arraycopy(notReadyInventory, 0, inventory, notReadyInventory.length - 9, 9);

        ItemStack[] armor = (ItemStack[]) savedPlayersInfo.get("armor");

        double health = (double) savedPlayersInfo.get("health");

        int foodLevel = (int) savedPlayersInfo.get("foodLevel");

        String[] potionEffects = (String[]) savedPlayersInfo.get("potionEffects");


        for (ItemStack item : inventory) {
            if (item == null) {
                contents.add(
                    GUIElementFactory.createActionItem(
                            new LoungeItem(Material.AIR).build(),
                            player -> {
                            },
                            player -> {
                            }
                    )
                );
            } else {
                contents.add(
                        GUIElementFactory.createActionItem(
                                item,
                                player -> {
                                },
                                player -> {
                                }
                        )
                );
            }
        }

        for (ItemStack item : armor) {
           if (item == null) {
                contents.add(
                    GUIElementFactory.createActionItem(
                            new LoungeItem(Material.AIR).build(),
                            player -> {
                            },
                            player -> {
                            }
                    )
                );
            } else {
                contents.add(
                        GUIElementFactory.createActionItem(
                                item,
                                player -> {
                                },
                                player -> {
                                }
                        )
                );
            }
        }

        contents.add(
            GUIElementFactory.createActionItem(
                    48,
                    new LoungeItem(Material.SKULL_ITEM, (int) health)
                            .withName(Lang.getString("ui.player-inventory.health.item-title", "<health>", health))
                            .build(),
                    player -> {},
                    player -> {}
            )
        );

        contents.add(
            GUIElementFactory.createActionItem(
                    49,
                    new LoungeItem(Material.COOKED_BEEF, foodLevel)
                            .withName(Lang.getString("ui.player-inventory.hunger.item-title", "<hunger>", foodLevel))
                            .build(),
                    player -> {},
                    player -> {}
            )
        );

        contents.add(
            GUIElementFactory.createActionItem(
                    50,
                    new LoungeItem(Material.BREWING_STAND_ITEM, potionEffects.length)
                            .withName(Lang.getString("ui.player-inventory.effects.item-title"))
                            .withLore(ChatUtil.colorize(potionEffects))
                            .build(),
                    player -> {},
                    player -> {}
            )
        );

        return contents;
    }
}
