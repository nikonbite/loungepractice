package me.nikonbite.api.lib.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        String[] parts = key.split(",");
        return Enchantment.getByName(parts[0].substring(parts[0].indexOf(']') + 1).toUpperCase());
    }
}


