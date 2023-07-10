package me.nikonbite.practice.kit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import me.nikonbite.api.LoungeAPI;
import me.nikonbite.api.lib.deserializer.ItemStackDeserializer;
import me.nikonbite.api.lib.item.LoungeItem;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.util.ChatUtil;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.kit.loadout.KitLoadout;
import me.nikonbite.practice.user.LoungePlayer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitManager {

    /// NoDebuff
    public static final Map<Integer, ItemStack> NODEBUFF_KIT_CONTENTS = new HashMap<>();
    public static final Map<Integer, ItemStack> NODEBUFF_KIT_ARMOR = new HashMap<>();
    public static final Map<Integer, ItemStack> NODEBUFF_KIT_EXTRAS = new HashMap<>();

    /// Debuff
    public static final Map<Integer, ItemStack> DEBUFF_KIT_CONTENTS = new HashMap<>();
    public static final Map<Integer, ItemStack> DEBUFF_KIT_ARMOR = new HashMap<>();
    public static final Map<Integer, ItemStack> DEBUFF_KIT_EXTRAS = new HashMap<>();

    /// Vanilla
    public static final Map<Integer, ItemStack> VANILLA_KIT_CONTENTS = new HashMap<>();
    public static final Map<Integer, ItemStack> VANILLA_KIT_ARMOR = new HashMap<>();
    public static final Map<Integer, ItemStack> VANILLA_KIT_EXTRAS = new HashMap<>();

    /// Axe
    public static final Map<Integer, ItemStack> AXE_KIT_CONTENTS = new HashMap<>();
    public static final Map<Integer, ItemStack> AXE_KIT_ARMOR = new HashMap<>();
    public static final Map<Integer, ItemStack> AXE_KIT_EXTRAS = new HashMap<>();

    /// Gapple
    public static final Map<Integer, ItemStack> GAPPLE_KIT_CONTENTS = new HashMap<>();
    public static final Map<Integer, ItemStack> GAPPLE_KIT_ARMOR = new HashMap<>();
    public static final Map<Integer, ItemStack> GAPPLE_KIT_EXTRAS = new HashMap<>();

    public static void giveKit(LoungePlayer player, Kit kit) {
        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(new ItemStack[]{null, null, null, null});

        inventory.setHelmet(kit.getArmor().get(0));
        inventory.setChestplate(kit.getArmor().get(1));
        inventory.setLeggings(kit.getArmor().get(2));
        inventory.setBoots(kit.getArmor().get(3));

        for (Map.Entry<Integer, ItemStack> entry : kit.getContents().entrySet()) {
            int slot = entry.getKey();
            ItemStack itemStack = entry.getValue();
            inventory.setItem(slot, itemStack);
        }
    }

    public static void giveKit(LoungePlayer player, Kit kit, ItemStack... customKit) {
        player.clearFullInventory();
        player.fillArmor(kit.getArmor().get(3), kit.getArmor().get(2), kit.getArmor().get(1), kit.getArmor().get(0));

        for (int i = 0; i < customKit.length; i++) {
            player.getInventory().setItem(i, customKit[i]);
        }
    }

    public static void giveKitSelector(LoungePlayer player, Kit kit) {

        player.getInventory().setItem(0,
                new LoungeItem(Material.ENCHANTED_BOOK, 1, (player1, block) -> giveKit((LoungePlayer) LoungePlayer.of(player1), kit))
                        .withName(ChatUtil.colorize("&eDefault Kit")).build());

        for (int i = 0; i < 7; i++) {
            int finalI = i;

            if (player.getKitLoadout() != null && player.getKitLoadout().getCustomKit(kit).size() > finalI && player.getKitLoadout().getCustomKit(kit).get(finalI) != null) {
                player.getInventory().setItem(i + 1,
                        new LoungeItem(Material.ENCHANTED_BOOK, 1, (player1, block) -> {
                            LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(player1);
                            giveKit(loungePlayer, kit, loungePlayer.getKitLoadout().getCustomKit(kit).get(finalI));
                        }).withName(ChatUtil.colorize("&eKIT: " + kit.getName() + " #" + (finalI + 1))).build());
            }
        }

    }

    public static String convertListToJson(List<ItemStack[]> list) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ItemStack[]> convertJsonToList(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ItemStack.class, new ItemStackDeserializer());
        objectMapper.registerModule(module);

        try {
            return objectMapper.readValue(json, new TypeReference<List<ItemStack[]>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static KitLoadout getPlayerKits(int playerId) {
        KitLoadout kitLoadout = new KitLoadout((LoungePlayer) LoungePlayer.of(GamerRegistry.get(playerId)));

        Arrays.stream(Kit.values()).forEach(kit -> {
            LoungeAPI.hikariConnection.executeQuery("SELECT `" + kit.getName() + "` FROM `CustomKits` WHERE `Id`=?", rs -> {
                if (rs.next()) {
                    String content = rs.getString(kit.getName());
                    if (content != null) {
                        List<ItemStack[]> list = convertJsonToList(content);
                        kitLoadout.setCustomKit(kit, list);
                    }
                }
                return null;
            }, playerId);
        });

        return kitLoadout;
    }


    public static void savePlayerKits(int playerId, KitLoadout kitLoadout) {
        String query = "UPDATE `CustomKits` SET `NoDebuff` = ?, `Debuff` = ?, `Vanilla` = ?, `Axe` = ?, `Gapple` = ? WHERE `Id` = ?";

        List<ItemStack[]> noDebuff = kitLoadout.getCustomKit(Kit.NODEBUFF);
        List<ItemStack[]> debuff = kitLoadout.getCustomKit(Kit.DEBUFF);
        List<ItemStack[]> vanilla = kitLoadout.getCustomKit(Kit.VANILLA);
        List<ItemStack[]> axe = kitLoadout.getCustomKit(Kit.AXE);
        List<ItemStack[]> gapple = kitLoadout.getCustomKit(Kit.GAPPLE);

        Object[] params = new Object[]{
                convertListToJson(noDebuff),
                convertListToJson(debuff),
                convertListToJson(vanilla),
                convertListToJson(axe),
                convertListToJson(gapple),
                playerId
        };

        LoungeAPI.hikariConnection.execute(query, params);
    }


    public static KitLoadout insertKits(LoungePlayer user) {
        StringBuilder query = new StringBuilder("INSERT INTO `CustomKits` VALUES (?, ");
        Kit[] kits = Kit.values();
        for (int i = 0; i < kits.length; i++) {
            query.append("?");
            if (i < kits.length - 1) {
                query.append(", ");
            }
        }
        query.append(") ON DUPLICATE KEY UPDATE ");
        for (int i = 0; i < kits.length; i++) {
            query.append("`").append(kits[i].name()).append("` = VALUES(`").append(kits[i].name()).append("`)");
            if (i < kits.length - 1) {
                query.append(", ");
            }
        }

        Object[] params = new Object[kits.length + 1];
        params[0] = user.getId();
        for (int i = 1; i <= kits.length; i++) {
            params[i] = null;
        }

        LoungeAPI.hikariConnection.execute(query.toString(), params);

        return new KitLoadout(user);
    }

    static {
        // ==================================================================================================================== \\
        /// NoDebuff Items
        NODEBUFF_KIT_CONTENTS.put(0, new LoungeItem(Material.DIAMOND_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 3).withEnchant(Enchantment.DURABILITY, 3).withEnchant(Enchantment.FIRE_ASPECT, 2).build());
        NODEBUFF_KIT_CONTENTS.put(1, new LoungeItem(Material.ENDER_PEARL, 16).build());

        NODEBUFF_KIT_CONTENTS.put(2, new LoungeItem(Material.POTION).withFireResistancePotion().build());
        NODEBUFF_KIT_CONTENTS.put(3, new LoungeItem(Material.POTION).withSpeedPotion().build());

        NODEBUFF_KIT_CONTENTS.put(8, new LoungeItem(Material.COOKED_BEEF, 64).build());

        NODEBUFF_KIT_CONTENTS.put(17, new LoungeItem(Material.POTION).withSpeedPotion().build());
        NODEBUFF_KIT_CONTENTS.put(26, new LoungeItem(Material.POTION).withSpeedPotion().build());
        NODEBUFF_KIT_CONTENTS.put(35, new LoungeItem(Material.POTION).withSpeedPotion().build());

        for (int i = 0; i < 36; i++) {
            if (i == 0 || i == 1 || i == 2 || i == 3 || i == 8 || i == 17 || i == 26 || i == 35) continue;
            NODEBUFF_KIT_CONTENTS.put(i, new LoungeItem(Material.POTION).withInstantHealthPotion().build());
        }

        /// NoDebuff Armor
        NODEBUFF_KIT_ARMOR.put(0, new LoungeItem(Material.DIAMOND_HELMET)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        NODEBUFF_KIT_ARMOR.put(1, new LoungeItem(Material.DIAMOND_CHESTPLATE)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        NODEBUFF_KIT_ARMOR.put(2, new LoungeItem(Material.DIAMOND_LEGGINGS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        NODEBUFF_KIT_ARMOR.put(3, new LoungeItem(Material.DIAMOND_BOOTS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .withEnchant(Enchantment.PROTECTION_FALL, 4)
                .build());

        /// NoDebuff Extras
        NODEBUFF_KIT_EXTRAS.put(0, new LoungeItem(Material.POTION).withInstantHealthPotion().build());
        NODEBUFF_KIT_EXTRAS.put(1, new LoungeItem(Material.POTION).withSpeedPotion().build());
        NODEBUFF_KIT_EXTRAS.put(2, new LoungeItem(Material.POTION).withFireResistancePotion().build());
        NODEBUFF_KIT_EXTRAS.put(3, new LoungeItem(Material.COOKED_BEEF, 64).build());
        NODEBUFF_KIT_EXTRAS.put(4, new LoungeItem(Material.GRILLED_PORK, 64).build());
        NODEBUFF_KIT_EXTRAS.put(5, new LoungeItem(Material.GOLDEN_CARROT, 64).build());

        // ==================================================================================================================== \\


        // ==================================================================================================================== \\
        /// Debuff Items
        DEBUFF_KIT_CONTENTS.put(0, new LoungeItem(Material.DIAMOND_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 3).withEnchant(Enchantment.DURABILITY, 3).withEnchant(Enchantment.FIRE_ASPECT, 2).build());
        DEBUFF_KIT_CONTENTS.put(1, new LoungeItem(Material.ENDER_PEARL, 16).build());

        DEBUFF_KIT_CONTENTS.put(2, new LoungeItem(Material.POTION).withFireResistancePotion().build());
        DEBUFF_KIT_CONTENTS.put(3, new LoungeItem(Material.POTION).withSpeedPotion().build());

        DEBUFF_KIT_CONTENTS.put(8, new LoungeItem(Material.GOLDEN_CARROT, 64).build());

        DEBUFF_KIT_CONTENTS.put(17, new LoungeItem(Material.POTION).withSpeedPotion().build());
        DEBUFF_KIT_CONTENTS.put(26, new LoungeItem(Material.POTION).withSpeedPotion().build());
        DEBUFF_KIT_CONTENTS.put(35, new LoungeItem(Material.POTION).withSpeedPotion().build());

        DEBUFF_KIT_CONTENTS.put(4, new LoungeItem(Material.POTION).withPoisonPotion().build());
        DEBUFF_KIT_CONTENTS.put(5, new LoungeItem(Material.POTION).withSlownessPotion().build());
        DEBUFF_KIT_CONTENTS.put(9, new LoungeItem(Material.POTION).withPoisonPotion().build());
        DEBUFF_KIT_CONTENTS.put(10, new LoungeItem(Material.POTION).withSlownessPotion().build());
        DEBUFF_KIT_CONTENTS.put(18, new LoungeItem(Material.POTION).withPoisonPotion().build());
        DEBUFF_KIT_CONTENTS.put(19, new LoungeItem(Material.POTION).withSlownessPotion().build());

        for (int i = 0; i < 36; i++) {
            if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 8 || i == 9 || i == 10 || i == 17 || i == 18 || i == 19 || i == 26 || i == 35)
                continue;
            DEBUFF_KIT_CONTENTS.put(i, new LoungeItem(Material.POTION).withInstantHealthPotion().build());
        }

        /// Debuff Armor
        DEBUFF_KIT_ARMOR.put(0, new LoungeItem(Material.DIAMOND_HELMET)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        DEBUFF_KIT_ARMOR.put(1, new LoungeItem(Material.DIAMOND_CHESTPLATE)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        DEBUFF_KIT_ARMOR.put(2, new LoungeItem(Material.DIAMOND_LEGGINGS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        DEBUFF_KIT_ARMOR.put(3, new LoungeItem(Material.DIAMOND_BOOTS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .withEnchant(Enchantment.PROTECTION_FALL, 4)
                .build());

        /// NoDebuff Extras
        DEBUFF_KIT_EXTRAS.put(0, new LoungeItem(Material.POTION).withInstantHealthPotion().build());
        DEBUFF_KIT_EXTRAS.put(1, new LoungeItem(Material.POTION).withPoisonPotion().build());
        DEBUFF_KIT_EXTRAS.put(2, new LoungeItem(Material.POTION).withSlownessPotion().build());
        DEBUFF_KIT_EXTRAS.put(3, new LoungeItem(Material.POTION).withSpeedPotion().build());
        DEBUFF_KIT_EXTRAS.put(4, new LoungeItem(Material.POTION).withFireResistancePotion().build());
        DEBUFF_KIT_EXTRAS.put(5, new LoungeItem(Material.COOKED_BEEF, 64).build());
        DEBUFF_KIT_EXTRAS.put(6, new LoungeItem(Material.GRILLED_PORK, 64).build());
        DEBUFF_KIT_EXTRAS.put(7, new LoungeItem(Material.GOLDEN_CARROT, 64).build());
        // ==================================================================================================================== \\

        // ==================================================================================================================== \\
        /// Axe Items
        AXE_KIT_CONTENTS.put(0, new LoungeItem(Material.IRON_AXE).withEnchant(Enchantment.DAMAGE_ALL, 1).build());
        AXE_KIT_CONTENTS.put(1, new LoungeItem(Material.POTION).withSpeedPotion().build());
        AXE_KIT_CONTENTS.put(2, new LoungeItem(Material.GOLDEN_APPLE, 16).build());

        for (int i = 3; i < 9; i++)
            AXE_KIT_CONTENTS.put(i, new LoungeItem(Material.POTION).withInstantHealthPotion().build());

        AXE_KIT_CONTENTS.put(34, new LoungeItem(Material.POTION).withInstantHealthPotion().build());
        AXE_KIT_CONTENTS.put(35, new LoungeItem(Material.POTION).withSpeedPotion().build());

        /// Axe Armor
        AXE_KIT_ARMOR.put(0, new LoungeItem(Material.IRON_HELMET).build());
        AXE_KIT_ARMOR.put(1, new LoungeItem(Material.IRON_CHESTPLATE).build());
        AXE_KIT_ARMOR.put(2, new LoungeItem(Material.IRON_LEGGINGS).build());
        AXE_KIT_ARMOR.put(3, new LoungeItem(Material.IRON_BOOTS).build());

        AXE_KIT_EXTRAS.put(1, new LoungeItem(Material.COOKED_BEEF, 64).build());
        AXE_KIT_EXTRAS.put(2, new LoungeItem(Material.GRILLED_PORK, 64).build());
        AXE_KIT_EXTRAS.put(3, new LoungeItem(Material.GOLDEN_CARROT, 64).build());
        // ==================================================================================================================== \\


        // ==================================================================================================================== \\
        /// Gapple Items
        GAPPLE_KIT_CONTENTS.put(0, new LoungeItem(Material.DIAMOND_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 5).withEnchant(Enchantment.DURABILITY, 3).withEnchant(Enchantment.FIRE_ASPECT, 2).build());
        GAPPLE_KIT_CONTENTS.put(1, new LoungeItem(new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1)).build());

        GAPPLE_KIT_CONTENTS.put(2, new LoungeItem(Material.DIAMOND_HELMET)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        GAPPLE_KIT_CONTENTS.put(3, new LoungeItem(Material.DIAMOND_CHESTPLATE)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        GAPPLE_KIT_CONTENTS.put(4, new LoungeItem(Material.DIAMOND_LEGGINGS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        GAPPLE_KIT_CONTENTS.put(5, new LoungeItem(Material.DIAMOND_BOOTS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .withEnchant(Enchantment.DURABILITY, 3)
                .withEnchant(Enchantment.PROTECTION_FALL, 4)
                .build());

        GAPPLE_KIT_CONTENTS.put(6, new LoungeItem(Material.POTION).withBigSpeedPotion().build());
        GAPPLE_KIT_CONTENTS.put(7, new LoungeItem(Material.POTION).withBigStrengthPotion().build());

        /// Gapple Armor
        GAPPLE_KIT_ARMOR.put(0, new LoungeItem(Material.DIAMOND_HELMET)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        GAPPLE_KIT_ARMOR.put(1, new LoungeItem(Material.DIAMOND_CHESTPLATE)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        GAPPLE_KIT_ARMOR.put(2, new LoungeItem(Material.DIAMOND_LEGGINGS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        GAPPLE_KIT_ARMOR.put(3, new LoungeItem(Material.DIAMOND_BOOTS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                .withEnchant(Enchantment.DURABILITY, 3)
                .withEnchant(Enchantment.PROTECTION_FALL, 4)
                .build());

        // ==================================================================================================================== \\

        // ==================================================================================================================== \\
        /// Vanilla Items
        VANILLA_KIT_CONTENTS.put(0, new LoungeItem(Material.DIAMOND_SWORD).withEnchant(Enchantment.DAMAGE_ALL, 5).withEnchant(Enchantment.DURABILITY, 3).withEnchant(Enchantment.FIRE_ASPECT, 2).build());
        VANILLA_KIT_CONTENTS.put(1, new LoungeItem(Material.ENDER_PEARL, 16).build());

        VANILLA_KIT_CONTENTS.put(2, new LoungeItem(Material.POTION).withFireResistancePotion().build());
        VANILLA_KIT_CONTENTS.put(3, new LoungeItem(Material.POTION).withSpeedPotion().build());
        VANILLA_KIT_CONTENTS.put(4, new LoungeItem(Material.POTION).withVanillaStrengthPotion().build());
        VANILLA_KIT_CONTENTS.put(5, new LoungeItem(Material.POTION).withVanillaRegenerationPotion().build());

        VANILLA_KIT_CONTENTS.put(8, new LoungeItem(Material.COOKED_BEEF, 64).build());

        VANILLA_KIT_CONTENTS.put(9, new LoungeItem(Material.POTION).withVanillaSlownessPotion().build());
        VANILLA_KIT_CONTENTS.put(10, new LoungeItem(Material.POTION).withVanillaSlownessPotion().build());

        VANILLA_KIT_CONTENTS.put(18, new LoungeItem(Material.POTION).withVanillaSplashRegenerationPotion().build());
        VANILLA_KIT_CONTENTS.put(19, new LoungeItem(Material.POTION).withVanillaWeaknessPotion().build());

        VANILLA_KIT_CONTENTS.put(24, new LoungeItem(Material.POTION).withVanillaRegenerationPotion().build());
        VANILLA_KIT_CONTENTS.put(25, new LoungeItem(Material.POTION).withSpeedPotion().build());
        VANILLA_KIT_CONTENTS.put(26, new LoungeItem(Material.POTION).withVanillaStrengthPotion().build());

        VANILLA_KIT_CONTENTS.put(27, new LoungeItem(Material.POTION).withPoisonPotion().build());
        VANILLA_KIT_CONTENTS.put(28, new LoungeItem(Material.POTION).withVanillaPoisonPotion().build());

        VANILLA_KIT_CONTENTS.put(33, new LoungeItem(Material.POTION).withVanillaRegenerationPotion().build());
        VANILLA_KIT_CONTENTS.put(34, new LoungeItem(Material.POTION).withSpeedPotion().build());
        VANILLA_KIT_CONTENTS.put(35, new LoungeItem(Material.POTION).withVanillaStrengthPotion().build());

        Arrays.stream(new int[]{6, 7, 11, 12, 13, 14, 15, 16, 17, 20, 21, 22, 23, 29, 30, 31, 32}).forEach(value ->
                VANILLA_KIT_CONTENTS.put(value, new LoungeItem(Material.POTION).withInstantHealthPotion().build()));

        /// Vanilla Armor
        VANILLA_KIT_ARMOR.put(0, new LoungeItem(Material.DIAMOND_HELMET)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        VANILLA_KIT_ARMOR.put(1, new LoungeItem(Material.DIAMOND_CHESTPLATE)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        VANILLA_KIT_ARMOR.put(2, new LoungeItem(Material.DIAMOND_LEGGINGS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .build());
        VANILLA_KIT_ARMOR.put(3, new LoungeItem(Material.DIAMOND_BOOTS)
                .withEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .withEnchant(Enchantment.DURABILITY, 3)
                .withEnchant(Enchantment.PROTECTION_FALL, 4)
                .build());

        /// Vanilla Extras

        VANILLA_KIT_EXTRAS.put(0, new LoungeItem(Material.POTION).withSpeedPotion().build());
        VANILLA_KIT_EXTRAS.put(1, new LoungeItem(Material.POTION).withFireResistancePotion().build());

        VANILLA_KIT_EXTRAS.put(2, new LoungeItem(Material.POTION).withSlownessPotion().build());

        VANILLA_KIT_EXTRAS.put(3, new LoungeItem(Material.POTION).withPoisonPotion().build());
        VANILLA_KIT_EXTRAS.put(4, new LoungeItem(Material.POTION).withVanillaPoisonPotion().build());

        VANILLA_KIT_EXTRAS.put(5, new LoungeItem(Material.POTION).withVanillaSplashRegenerationPotion().build());
        VANILLA_KIT_EXTRAS.put(6, new LoungeItem(Material.POTION).withVanillaWeaknessPotion().build());

        VANILLA_KIT_EXTRAS.put(7, new LoungeItem(Material.POTION).withVanillaRegenerationPotion().build());
        VANILLA_KIT_EXTRAS.put(8, new LoungeItem(Material.POTION).withVanillaStrengthPotion().build());

        VANILLA_KIT_EXTRAS.put(9, new LoungeItem(Material.POTION).withInstantHealthPotion().build());
        VANILLA_KIT_EXTRAS.put(10, new LoungeItem(Material.COOKED_BEEF, 64).build());
        VANILLA_KIT_EXTRAS.put(11, new LoungeItem(Material.GRILLED_PORK, 64).build());
        VANILLA_KIT_EXTRAS.put(12, new LoungeItem(Material.GOLDEN_CARROT, 64).build());
        // ==================================================================================================================== \\
    }
}
