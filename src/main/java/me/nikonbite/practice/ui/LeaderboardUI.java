package me.nikonbite.practice.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.nikonbite.api.LoungeAPI;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.api.lib.item.LoungeItem;
import me.nikonbite.api.lib.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;

public class LeaderboardUI {
    private InventoryGUI gui;
    private final LoungePlayer player;

    public LeaderboardUI(LoungePlayer player) {
        this.player = player;
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(27)
                .contents(Lang.getString("ui.leaderboard.title"),
                        genContents(),
                        true,
                        false,
                        false
                )
                .build();
    }

    public void showGUI() {
        gui.open(player);
    }

    protected List<GUIElement> genContents() {
        List<GUIElement> contents = new ArrayList<>();

        contents.add(
                GUIElementFactory.createActionItem(
                        4,
                        new LoungeItem(Material.NETHER_STAR)
                                .withName(ChatUtil.colorize("&7TOP 10: &a&lGlobal"))
                                .withLore(generateGlobalLeaderboard())
                                .build(),
                        event -> {
                        }
                )
        );

        for (Kit kit : Kit.values()) {
            contents.add(
                    GUIElementFactory.createActionItem(
                            kit.ordinal() + 11,
                            new LoungeItem(kit.getIcon())
                                    .withName(ChatUtil.colorize("&a&l" + kit.getName()))
                                    .withLore(generateLeaderboard(kit))
                                    .build(),
                            event -> {
                            }
                    )
            );
        }

        return contents;
    }

    public List<String> generateLeaderboard(Kit kit) {
        List<String> stats = new ArrayList<>();

        Map<String, Integer> topPlayersByElo = new HashMap<>();

        LoungeAPI.hikariConnection.executeQuery("SELECT * FROM `Statistics`", rs -> {
            while (rs.next()) {
                String playerName = rs.getString("Name");
                String kitJson = rs.getString(kit.getName());

                if (kitJson != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    TypeReference<Map<String, Integer>> typeRef = new TypeReference<Map<String, Integer>>() {};
                    Map<String, Integer> kitMap = mapper.readValue(kitJson, typeRef);

                    int elo = kitMap.getOrDefault("elo", 1000); // по умолчанию elo = 1000

                    topPlayersByElo.put(playerName, elo);
                }
            }

            return null;
        });

        List<Map.Entry<String, Integer>> sortedTopPlayersByElo = new ArrayList<>(topPlayersByElo.entrySet());
        sortedTopPlayersByElo.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedTopPlayersByElo) {
            if (count >= 10) {
                break;
            }

            stats.add("&e#" + (count + 1) + ". &f" + entry.getKey() + " &7-&e&l " + entry.getValue() + " ELO");
            count++;
        }


        return ChatUtil.colorize(stats);
    }

    public List<String> generateGlobalLeaderboard() {
        List<String> stats = new ArrayList<>();

        Map<String, Integer> topPlayersByElo = new HashMap<>();

        LoungeAPI.hikariConnection.executeQuery("SELECT * FROM `Statistics`", rs -> {
            while (rs.next()) {
                String playerName = rs.getString("Name");

                int totalElo = 0;
                for (Kit kit : Kit.values()) {
                    String kitJson = rs.getString(kit.getName());

                    if (kitJson != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        TypeReference<Map<String, Integer>> typeRef = new TypeReference<Map<String, Integer>>() {};
                        Map<String, Integer> kitMap = mapper.readValue(kitJson, typeRef);

                        int elo = kitMap.getOrDefault("elo", 1000); // по умолчанию elo = 1000
                        totalElo += elo;
                    }
                }

                topPlayersByElo.put(playerName, (totalElo / Kit.values().length));
            }

            return null;
        });

        List<Map.Entry<String, Integer>> sortedTopPlayersByElo = new ArrayList<>(topPlayersByElo.entrySet());
        sortedTopPlayersByElo.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedTopPlayersByElo) {
            if (count >= 10) {
                break;
            }

            stats.add("&e#" + (count + 1) + ". &f" + entry.getKey() + " &7-&e&l " + entry.getValue() + " ELO");
            count++;
        }

        return ChatUtil.colorize(stats);
    }

}