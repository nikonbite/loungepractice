package me.nikonbite.whitelist.data;

import com.google.gson.Gson;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PlayerData {

    private Map<String, PlayerDataEntry> data;
    private final File dataFile;
    private final Gson gson;

    public PlayerData(File dataFile) {
        this.dataFile = dataFile;
        this.gson = new Gson();
        this.data = new HashMap<>(loadAll());
    }

    public PlayerData() {
        this(new File("../../lounge-files/players.json"));
    }

    public Map<String, PlayerDataEntry> loadAll() {
        if (!dataFile.exists()) {
            return new HashMap<>();
        }
        try (FileReader reader = new FileReader(dataFile)) {
            return gson.fromJson(reader, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public void saveAll(Map<String, PlayerDataEntry> data) {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(data, HashMap.class, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}