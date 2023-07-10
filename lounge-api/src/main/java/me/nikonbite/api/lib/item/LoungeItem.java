package me.nikonbite.api.lib.item;

import lombok.Getter;
import me.nikonbite.api.lib.item.manager.ItemManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

@Getter
public class LoungeItem {

    private ItemStack item;
    private ItemMeta meta;
    private BiConsumer<Player, Block> onClick;

    public LoungeItem(Material material) {
        this(material, 1);
    }

    public LoungeItem(ItemStack itemStack) {
        item = itemStack;
        meta = item.getItemMeta();
    }

    public LoungeItem(Material material, int amount, BiConsumer<Player, Block> onClick) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
        this.onClick = onClick;
    }

    public LoungeItem(ItemStack itemStack, BiConsumer<Player, Block> onClick) {
        item = itemStack;
        meta = item.getItemMeta();
        this.onClick = onClick;
    }

    public LoungeItem(Material material, int amount) {
        item = new ItemStack(material, amount);
        meta = item.getItemMeta();
    }

    public LoungeItem withName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public LoungeItem withLore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public LoungeItem withEnchant(Enchantment enchant, int level) {
        meta.addEnchant(enchant, level, true);
        return this;
    }

    public LoungeItem withFireResistancePotion() {
        Potion potion = new Potion(PotionType.FIRE_RESISTANCE, 1);
        potion.setSplash(false);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 8259);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withSpeedPotion() {
        Potion potion = new Potion(PotionType.SPEED, 2);
        potion.setSplash(false);
        ItemStack potionItem = potion.toItemStack(1);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withBigSpeedPotion() {
        Potion potion = new Potion(PotionType.SPEED, 2);
        potion.setSplash(false);

        ItemStack potionItem = potion.toItemStack(1);

        PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 8 * 60 * 20, 1), true);
        potionItem.setItemMeta(meta);

        item = potionItem;
        return this;
    }

    public LoungeItem withBigStrengthPotion() {
        Potion potion = new Potion(PotionType.STRENGTH, 2);
        potion.setSplash(false);

        ItemStack potionItem = potion.toItemStack(1);

        PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8 * 60 * 20, 1), true);
        potionItem.setItemMeta(meta);

        item = potionItem;
        return this;
    }


    public LoungeItem withInstantHealthPotion() {
        Potion potion = new Potion(PotionType.INSTANT_HEAL, 2);
        potion.setSplash(true);
        ItemStack potionItem = potion.toItemStack(1);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withPoisonPotion() {
        Potion potion = new Potion(PotionType.POISON, 2);
        potion.setSplash(true);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 16388);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withVanillaPoisonPotion() {
        Potion potion = new Potion(PotionType.POISON, 2);
        potion.setSplash(true);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 16420);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withVanillaSplashRegenerationPotion() {
        Potion potion = new Potion(PotionType.REGEN, 2);
        potion.setSplash(true);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 16417);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withVanillaRegenerationPotion() {
        Potion potion = new Potion(PotionType.REGEN, 1);
        potion.setSplash(false);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 8257);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withVanillaStrengthPotion() {
        Potion potion = new Potion(PotionType.STRENGTH, 2);
        potion.setSplash(false);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 8233);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withSlownessPotion() {
        Potion potion = new Potion(PotionType.SLOWNESS, 2);
        potion.setSplash(true);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 16426);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withVanillaSlownessPotion() {
        Potion potion = new Potion(PotionType.SLOWNESS, 1);
        potion.setSplash(true);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 16458);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withVanillaWeaknessPotion() {
        Potion potion = new Potion(PotionType.WEAKNESS, 1);
        potion.setSplash(true);
        ItemStack potionItem = potion.toItemStack(1);
        potionItem.setDurability((short) 16456);
        item = potionItem;
        meta = potionItem.getItemMeta();
        return this;
    }

    public LoungeItem withLore(String... lore) {
        List<String> loreList = new ArrayList<>();
        Collections.addAll(loreList, lore);
        return withLore(loreList);
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }


    {
        ItemManager.REGISTERED_ITEMS.add(this);
    }
}