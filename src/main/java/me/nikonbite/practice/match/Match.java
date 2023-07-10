package me.nikonbite.practice.match;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.arena.Arena;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.kit.KitManager;
import me.nikonbite.practice.match.type.MatchType;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.util.ChatUtil;
import me.nikonbite.api.lib.util.LocationUtil;
import me.nikonbite.api.lib.util.NumberUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@NoArgsConstructor
public class Match {

    public static List<Match> matchList = new ArrayList<>();
    public static List<Arena> arenaList = new ArrayList<>();
    private final LoungePractice plugin = LoungePractice.instance;
    public List<LoungePlayer> spectators;

    private LoungePlayer player1;
    private LoungePlayer player2;

    private MatchType matchType;
    private Arena arena;
    private Kit kit;

    private boolean active;
    private boolean waiting;
    private int countdownTaskId;

    public Match(LoungePlayer player1, LoungePlayer player2, MatchType matchType, Kit kit, Arena arena) {
        this.player1 = player1;
        this.player2 = player2;
        this.matchType = matchType;
        this.kit = kit;
        this.arena = arena;

        spectators = new ArrayList<>();
    }

    public static int getPlayersCountInMatch(Kit kit, MatchType matchType) {
        AtomicInteger count = new AtomicInteger();

        matchList.forEach(match -> {
            if (match.getKit() == kit && match.getMatchType() == matchType) count.getAndIncrement();
        });

        return count.get() * 2;
    }

    public void start() {
        if (active) return;
        active = true;

        player1.setMatch(this);
        player2.setMatch(this);

        matchList.add(this);

        if (arenaList.contains(arena)) {
            List<Arena> newArenas = new ArrayList<>(Arena.arenas);
            newArenas.remove(arena);

            arena = newArenas.get(new Random().nextInt(newArenas.size()));
        }

        arenaList.add(arena);
        player1.teleport(LocationUtil.stringToLocation(arena.getFirstPlayerSpawn()));
        player2.teleport(LocationUtil.stringToLocation(arena.getSecondPlayerSpawn()));

        player1.removeDrops();
        player2.removeDrops();

        player1.clearFullInventory();
        player2.clearFullInventory();

        player1.setGameMode(GameMode.SURVIVAL);
        player2.setGameMode(GameMode.SURVIVAL);

        player1.setStatement(Statement.MATCH);
        player2.setStatement(Statement.MATCH);

        player1.setHealth(20);
        player1.setFoodLevel(20);

        player2.setHealth(20);
        player2.setFoodLevel(20);

        // выдача набора
        KitManager.giveKitSelector(player1, kit);
        KitManager.giveKitSelector(player2, kit);

        if (matchType == MatchType.RANKED) {
            player1.sendKeyMessage("messages.match.opponent-ranked",
                    "<player>", player2.getName(),
                    "<kit>", kit.getName(),
                    "<elo>", player2.getStatistics().getElo(kit),
                    "<difference>", NumberUtil.findDifference(player1.getStatistics().getElo(kit), player2.getStatistics().getElo(kit))
            );

            player2.sendKeyMessage("messages.match.opponent-ranked",
                    "<player>", player1.getName(),
                    "<kit>", kit.getName(),
                    "<elo>", player1.getStatistics().getElo(kit),
                    "<difference>", NumberUtil.findDifference(player2.getStatistics().getElo(kit), player1.getStatistics().getElo(kit))
            );
        } else {
            player1.sendKeyMessage("messages.match.opponent", "<player>", player2.getName(), "<kit>", kit.getName());
            player2.sendKeyMessage("messages.match.opponent", "<player>", player1.getName(), "<kit>", kit.getName());
        }

        player1.sendKeyMessage("messages.match.now-playing", "<map>", arena.getName());
        player2.sendKeyMessage("messages.match.now-playing", "<map>", arena.getName());


        player1.setSaturation(6);
        player2.setSaturation(6);

        player1.freeze();
        player2.freeze();

        // начало отсчёта времени до игры
        countdownTaskId = new BukkitRunnable() {
            int secondsLeft = 5;

            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    startGame();
                    waiting = false;
                    player1.unfreeze();
                    player2.unfreeze();
                    cancel();
                    return;
                }

                waiting = true;

                // Отправка сообщения каждую секунду
                player1.sendKeyMessage("messages.match.timer", "<seconds>", secondsLeft);
                player2.sendKeyMessage("messages.match.timer", "<seconds>", secondsLeft);
                secondsLeft--;
            }
        }.runTaskTimer(LoungePractice.instance, 0, 20).getTaskId();

    }

    private void startGame() {
        player1.sendKeyMessage("messages.match.started");
        player2.sendKeyMessage("messages.match.started");

        player1.sendKeyMessage("messages.match.reminder");
        player2.sendKeyMessage("messages.match.reminder");
    }

    public void addSpectator(LoungePlayer spectator) {
        spectator.setStatement(Statement.SPECTATOR);
        spectator.setGameMode(GameMode.ADVENTURE);
        spectator.setAllowFlight(true);
        spectator.setFlying(true);

        spectator.showPlayer(player1);
        spectator.showPlayer(player2);
        player1.hidePlayer(spectator);
        player2.hidePlayer(spectator);

        spectator.teleport(LocationUtil.stringToLocation(arena.getSpectatorSpawn()));
        spectators.add(spectator);
    }

    public void removeSpectator(LoungePlayer spectator) {
        spectator.setStatement(Statement.LOBBY);
        spectator.setGameMode(GameMode.ADVENTURE);
        spectator.setAllowFlight(false);
        spectator.setFlying(false);

        spectator.sendToLobby();
        spectator.hidePlayer(player1);
        spectator.hidePlayer(player2);
        player1.hidePlayer(spectator);
        player2.hidePlayer(spectator);

        spectators.remove(spectator);
    }

    public void endMatch(LoungePlayer winner, LoungePlayer loser) {
        Bukkit.getScheduler().cancelTask(countdownTaskId);

        winner.setMatch(null);
        loser.setMatch(null);

        /// ELO Changes
        if (matchType == MatchType.RANKED) {
            /// For Winner
            int ratingChangeWinner = Math.round((float) winner.getStatistics().getElo(kit) / 120);
            int ratingChangeLoser = Math.round((float) loser.getStatistics().getElo(kit) / 120);

            winner.getStatistics().addElo(ratingChangeWinner, kit);
            loser.getStatistics().removeElo(ratingChangeLoser, kit);

            winner.sendKeyMessage("messages.match.elo-changes",
                    "<winner>", winner.getName(),
                    "<eloToAdd>", ratingChangeWinner,
                    "<newEloWinner>", winner.getStatistics().getElo(kit),
                    "<loser>", loser.getName(),
                    "<eloToRemove>", ratingChangeLoser,
                    "<newEloLoser>", loser.getStatistics().getElo(kit)
            );

            loser.sendKeyMessage("messages.match.elo-changes",
                    "<winner>", winner.getName(),
                    "<eloToAdd>", ratingChangeWinner,
                    "<newEloWinner>", winner.getStatistics().getElo(kit),
                    "<loser>", loser.getName(),
                    "<eloToRemove>", ratingChangeLoser,
                    "<newEloLoser>", loser.getStatistics().getElo(kit)
            );
        }

        TextComponent checkInventory = new TextComponent(Lang.getString("messages.match.inventory-message"));
        checkInventory.addExtra(ChatUtil.createClickableText("§a" + winner.getName(), "§aClick to view", "/checksavedinv " + winner.getName()));
        checkInventory.addExtra("§7, ");
        checkInventory.addExtra(ChatUtil.createClickableText("§c" + loser.getName(), "§cClick to view", "/checksavedinv " + loser.getName()));

        TextComponent rateMap = new TextComponent(Lang.getString("messages.match.map-rate"));
        rateMap.addExtra(ChatUtil.createClickableText("§2[5✮]", "§aClick to rate!", "/rate"));
        rateMap.addExtra(" ");
        rateMap.addExtra(ChatUtil.createClickableText("§a[4✮]", "§aClick to rate!", "/rate"));
        rateMap.addExtra(" ");
        rateMap.addExtra(ChatUtil.createClickableText("§e[3✮]", "§aClick to rate!", "/rate"));
        rateMap.addExtra(" ");
        rateMap.addExtra(ChatUtil.createClickableText("§c[2✮]", "§aClick to rate!", "/rate"));
        rateMap.addExtra(" ");
        rateMap.addExtra(ChatUtil.createClickableText("§4[1✮]", "§aClick to rate!", "/rate"));

        /// Winner
        winner.sendKeyMessage("messages.match.winner", "<winner>", winner.getName());
        winner.handle().spigot().sendMessage(checkInventory);
        winner.handle().spigot().sendMessage(rateMap);

        winner.setHealth(20);
        winner.setFoodLevel(20);
        winner.clearFullInventory();

        if (matchType == MatchType.UNRANKED || matchType == MatchType.RANKED)
            winner.getStatistics().addWin(kit);

        winner.removeEffects();
        winner.getHandle().getDataWatcher().watch(9, (byte) 0);
        winner.setFireTicks(0);
        winner.setGameMode(GameMode.ADVENTURE);

        long expirationTimeWinner = System.currentTimeMillis() + 3000;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > expirationTimeWinner) {
                    winner.setStatement(Statement.LOBBY);
                    winner.sendToLobby();
                    arenaList.remove(arena);
                    cancel();
                }
            }
        }.runTaskTimer(LoungePractice.instance, 0L, 20L);

        List<LoungePlayer> spectatorCopy = new ArrayList<>(spectators);
        spectatorCopy.forEach(loungePlayer -> {
            loungePlayer.sendKeyMessage("messages.match.winner", "<winner>", winner.getName());
            loungePlayer.handle().spigot().sendMessage(checkInventory);
            removeSpectator(loungePlayer);
        });


        /// Loser
        loser.sendKeyMessage("messages.match.winner", "<winner>", winner.getName());
        loser.handle().spigot().sendMessage(checkInventory);
        loser.handle().spigot().sendMessage(rateMap);

        loser.setHealth(20);
        loser.setFoodLevel(20);

        if (matchType == MatchType.UNRANKED || matchType == MatchType.RANKED)
            loser.getStatistics().addLoss(kit);

        loser.removeEffects();
        loser.getHandle().getDataWatcher().watch(9, (byte) 0);
        loser.setFireTicks(0);
        loser.setGameMode(GameMode.ADVENTURE);

        long expirationTimeLoser = System.currentTimeMillis() + 100;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > expirationTimeLoser) {
                    loser.setStatement(Statement.LOBBY);
                    loser.sendToLobby();
                    cancel();
                }
            }
        }.runTaskTimer(LoungePractice.instance, 0L, 1L);

        active = false;
        matchList.remove(this);
    }
}