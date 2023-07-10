package me.nikonbite.api.lib.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ItemStackDeserializer extends JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        int amount = node.get("amount").asInt();
        Material material = Material.getMaterial(node.get("typeId").asInt());
        short damage = (short) node.get("durability").asInt();
        ItemStack itemStack = new ItemStack(material, amount, damage);

        JsonNode itemMetaNode = node.get("itemMeta");
        if (itemMetaNode != null && !itemMetaNode.isNull()) {
            ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);

            if (itemMetaNode.has("displayName") && !itemMetaNode.get("displayName").isNull()) {
                itemMeta.setDisplayName(itemMetaNode.get("displayName").asText());
            }
            if (itemMetaNode.has("lore") && !itemMetaNode.get("lore").isNull()) {
                itemMeta.setLore(Arrays.asList(itemMetaNode.get("lore").asText().split("\n")));
            }

            if (itemMetaNode.has("enchants") && !itemMetaNode.get("enchants").isNull()) {
                Map<String, Integer> enchantMap = new HashMap<>();
                JsonNode enchantNode = itemMetaNode.get("enchants");
                if (enchantNode != null) {
                    enchantNode.fields().forEachRemaining(entry -> {
                        String enchantNameWithPrefix = entry.getKey();
                        int prefixLength = "Enchantment[".length();
                        String enchantName = enchantNameWithPrefix.substring(prefixLength, enchantNameWithPrefix.indexOf(","));
                        enchantMap.put(enchantName, entry.getValue().asInt());
                    });
                }

                enchantMap.forEach((enchantName, level) -> {
                    int enchantId = Integer.parseInt(enchantName.split(",")[0].replace("Enchantment[", ""));
                    Enchantment enchant = Enchantment.getById(enchantId);
                    if (enchant != null) {
                        itemMeta.addEnchant(enchant, level, true);
                    }
                });
            }

            itemStack.setItemMeta(itemMeta);
        }
        return itemStack;
    }
}