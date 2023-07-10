package me.nikonbite.api.lib.deserializer;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class MaterialDataKeyDeserializer extends KeyDeserializer {

    @Override
    @SneakyThrows
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        String[] parts = key.split(",");
        Material material = Material.valueOf(parts[0].substring(parts[0].indexOf(']') + 1).toUpperCase());
        byte data = Byte.parseByte(parts[1]);
        return new MaterialData(material, data);
    }
}
