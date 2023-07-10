package me.nikonbite.api.lib.inventory.gui.elements;

import me.nikonbite.api.lib.inventory.util.Callback;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * Some helpful methods for creating common GUIElements
 */
public class GUIElementFactory {

    public static ItemStack formatItem(ItemStack base, String displayName, String... lore) {
        if (base == null || displayName == null || lore == null) {
            throw new IllegalArgumentException("No arguments to formatItem can be null!");
        }
        ItemMeta im = base.getItemMeta(); //This method returns a clone of the ItemMeta
/*        im.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        im.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        im.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);*/
        im.setDisplayName(displayName);
        im.setLore(Arrays.asList(lore));
        base.setItemMeta(im); //Update the ItemMeta on the item as our 'im' was just a clone of it
        return base;
    }

    public static InputSlot createInputSlot(String uniqueInputSlotID, InputSlot.ActionHandler actionHandler) {
        return createInputSlot(uniqueInputSlotID, AbstractGUIElement.NO_DESIRED_SLOT, actionHandler);
    }

    public static InputSlot createInputSlot(String uniqueInputSlotID, int desiredSlot, InputSlot.ActionHandler actionHandler) {
        //Args validated within constructor
        return new InputSlot(uniqueInputSlotID, desiredSlot, actionHandler);
    }

    public static ActionItem createActionItem(ItemStack displayItem, ActionItem.ActionHandler actionHandler) {
        return createActionItem(AbstractGUIElement.NO_DESIRED_SLOT, displayItem, actionHandler);
    }

    public static ActionItem createActionItem(ItemStack displayItem, Callback<Player> onLeftClick, Callback<Player> onRightClick) {
        return createActionItem(AbstractGUIElement.NO_DESIRED_SLOT, displayItem, onLeftClick, onRightClick);
    }

    public static ActionItem createActionItem(int desiredSlot, ItemStack displayItem, final Callback<Player> onLeftClick,
                                              final Callback<Player> onRightClick) {
        if (onLeftClick == null) {
            throw new IllegalArgumentException("Click task must not be null!");
        } else if (onRightClick == null) {
            throw new IllegalArgumentException("Click task must not be null!");
        }
        return createActionItem(desiredSlot, displayItem, event -> {
            event.getBukkitEvent().setCancelled(true);
            if (event.getBukkitEvent().getClick().isLeftClick()) {
                onLeftClick.call(event.getViewer());
            } else if (event.getBukkitEvent().getClick().isRightClick()) {
                onRightClick.call(event.getViewer());
            }
        });
    }

    public static ActionItem createClickableItem(ItemStack displayItem, Callback<Player> onClick) {
        return createClickableItem(displayItem, onClick, AbstractGUIElement.NO_DESIRED_SLOT);
    }

    public static ActionItem createClickableItem(ItemStack displayItem, final Callback<Player> onClick, int desiredSlot) {
        if (onClick == null) {
            throw new IllegalArgumentException("Click task must not be null!");
        }

        return createActionItem(desiredSlot, displayItem, event -> {
            event.getBukkitEvent().setCancelled(true);
            if (event.getBukkitEvent().getClick().isLeftClick() || event.getBukkitEvent().getClick().isRightClick()) {
                onClick.call(event.getViewer());
            }
        });
    }

    public static ActionItem createActionItem(int desiredSlot, ItemStack displayItem, ActionItem.ActionHandler actionHandler) {
        return new ActionItem(desiredSlot, displayItem, actionHandler);
    }
}
