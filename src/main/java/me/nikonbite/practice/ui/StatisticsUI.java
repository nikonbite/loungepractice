package me.nikonbite.practice.ui;

import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.practice.user.Statistics;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElement;
import me.nikonbite.api.lib.inventory.gui.elements.GUIElementFactory;
import me.nikonbite.api.lib.inventory.gui.guis.GUIBuilder;
import me.nikonbite.api.lib.inventory.gui.guis.InventoryGUI;
import me.nikonbite.api.lib.item.LoungeItem;
import me.nikonbite.api.lib.util.ChatUtil;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.List;

public class StatisticsUI {
    private InventoryGUI gui;
    private final LoungePlayer player;

    public StatisticsUI(LoungePlayer player) {
        this.player = player;
        createGUI();
    }

    protected void createGUI() {
        gui = new GUIBuilder()
                .guiStateBehaviour(GUIBuilder.GUIStateBehaviour.LOCAL_TO_SESSION)
                .inventoryType(InventoryType.CHEST)
                .dynamicallyResizeToWrapContent(false)
                .size(27)
                .contents(Lang.getString("ui.statistics.title"),
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
                        .withName(ChatUtil.colorize("&a&lGlobal Statistics"))
                        .withLore(generateGlobalStats())
                        .build(),
                    event -> {}
            )
        );

        for (Kit kit : Kit.values()) {
            contents.add(
                GUIElementFactory.createActionItem(
                        kit.ordinal() + 11,
                        new LoungeItem(kit.getIcon())
                            .withName(ChatUtil.colorize("&a&l" + kit.getName()))
                            .withLore(generateStats(kit))
                            .build(),
                        event -> {}
                )
            );
        }

        return contents;
    }

    public List<String> generateStats(Kit kit) {
        List<String> stats = new ArrayList<>();
        Statistics statistics = player.getStatistics();

        stats.add(" &7- &fELO: &e" + statistics.getElo(kit));
        stats.add(" &7- &fWins: &e" + statistics.getWins(kit));
        stats.add(" &7- &fLosses: &e" + statistics.getLosses(kit));

        return ChatUtil.colorize(stats);
    }

    public List<String> generateGlobalStats() {
        List<String> stats = new ArrayList<>();
        Statistics statistics = player.getStatistics();

        stats.add(" &7- &fELO: &e" + statistics.getGlobalElo());
        stats.add(" &7- &fWins: &e" + statistics.getGlobalWins());
        stats.add(" &7- &fLosses: &e" + statistics.getGlobalLosses());

        return ChatUtil.colorize(stats);
    }
}