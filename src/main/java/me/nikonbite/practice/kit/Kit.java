package me.nikonbite.practice.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nikonbite.api.lib.item.LoungeItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum Kit {
    GAPPLE("Gapple", KitManager.GAPPLE_KIT_CONTENTS, KitManager.GAPPLE_KIT_ARMOR, KitManager.GAPPLE_KIT_EXTRAS,
            new LoungeItem(Material.GOLDEN_APPLE).build()),
    NODEBUFF("NoDebuff", KitManager.NODEBUFF_KIT_CONTENTS, KitManager.NODEBUFF_KIT_ARMOR, KitManager.NODEBUFF_KIT_EXTRAS,
            new LoungeItem(Material.POTION).withInstantHealthPotion().build()),
    VANILLA("Vanilla", KitManager.VANILLA_KIT_CONTENTS, KitManager.VANILLA_KIT_ARMOR, KitManager.VANILLA_KIT_EXTRAS,
            new LoungeItem(Material.POTION).build()),
    AXE("Axe", KitManager.AXE_KIT_CONTENTS, KitManager.AXE_KIT_ARMOR, KitManager.AXE_KIT_EXTRAS,
            new LoungeItem(Material.IRON_AXE).build()),
    DEBUFF("Debuff", KitManager.DEBUFF_KIT_CONTENTS, KitManager.DEBUFF_KIT_ARMOR, KitManager.DEBUFF_KIT_EXTRAS,
            new LoungeItem(Material.POTION).withPoisonPotion().build());

    final String name;
    final Map<Integer, ItemStack> contents;
    final Map<Integer, ItemStack> armor;
    final Map<Integer, ItemStack> extras;
    final ItemStack icon;
}
