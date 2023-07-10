package me.nikonbite.practice.util;

import lombok.SneakyThrows;
import me.nikonbite.practice.user.LoungePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface ConvertationUtil {

    static String itemListToString(List<ItemStack[]> itemList) {
        StringBuilder itemListString = new StringBuilder();

        if (itemList == null) {
            return "";
        }

        for (ItemStack[] items : itemList) {
            itemListString.append(Arrays.deepToString(items)).append(";");
        }

        return itemListString.toString();
    }

    static List<ItemStack[]> parseItemList(String itemListString) {
        if (Objects.equals(itemListString, "")) {
            return null;
        }

        List<ItemStack[]> itemList = new ArrayList<>();
        String[] listStrings = itemListString.split(";"); // разделяем на отдельные списки
        for (String listString : listStrings) {
            String[] itemStrings = listString.split(","); // разделяем на отдельные элементы
            ItemStack[] items = new ItemStack[itemStrings.length];
            for (int i = 0; i < itemStrings.length; i++) {
                String[] itemData = itemStrings[i].split(":"); // разделяем данные элемента
                int typeId = Integer.parseInt(itemData[0]);
                int amount = Integer.parseInt(itemData[1]);
                byte data = Byte.parseByte(itemData[2]);
                ItemStack item = new ItemStack(typeId, amount, data);
                items[i] = item;
            }
            itemList.add(items);
        }
        return itemList;
    }


    @SneakyThrows
    static ByteArrayInputStream serializeItemsArray(ItemStack[] items) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeInt(items.length);

        // Write each item in the byte stream.

        // all the properties of ItemStack are serialized, even custom NBT tags.
        for (int i = 0; i < items.length; i++) {
            dataOutput.writeObject(items[i]);
        }

        dataOutput.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    @SneakyThrows
    static ItemStack[] deserializeItemsArray(InputStream inputStream) {
        if (inputStream == null || inputStream.available() == 0)
            return new ItemStack[0];
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
        ItemStack[] items = new ItemStack[dataInput.readInt()];

        // Read the serialized inventory
        for (int i = 0; i < items.length; i++) {
            items[i] = (ItemStack) dataInput.readObject();
        }

        dataInput.close();
        return items;
    }

    static String formatLoungePlayers(List<LoungePlayer> loungePlayers) {
        return loungePlayers.stream()
                .map(LoungePlayer::getName)
                .collect(Collectors.joining(", "));
    }
}
