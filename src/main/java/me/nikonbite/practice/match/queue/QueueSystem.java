package me.nikonbite.practice.match.queue;

import lombok.Getter;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.arena.Arena;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.match.Match;
import me.nikonbite.practice.match.type.MatchType;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.practice.user.LoungePlayer;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class QueueSystem {
    private final List<LoungePlayer> queuePlayerList = new ArrayList<>();
    private final Map<LoungePlayer, Kit> playerKitMap = new HashMap<>();
    private final Map<LoungePlayer, int[]> playerEloMap = new HashMap<>();
    private final Map<LoungePlayer, MatchType> playerMatchTypeMap = new HashMap<>();
    private final Map<LoungePlayer, List<Arena>> playerArenasMap = new HashMap<>();
    private final Map<LoungePlayer, Integer> playerTaskId = new HashMap<>();

    public void addPlayerToQueue(LoungePlayer player, Kit kit, MatchType matchType, List<Arena> arenas) {
        queuePlayerList.add(player);
        playerKitMap.put(player, kit);
        playerMatchTypeMap.put(player, matchType);
        playerArenasMap.put(player, arenas);

        player.setStatement(Statement.QUEUE);
        player.sendKeyMessage("messages.queue.added", "<queue-type>", matchType.getName() + " " + kit.getName());


        if (matchType == MatchType.RANKED) {
            int[] diapason = {player.getStatistics().getElo(kit), (player.getStatistics().getElo(kit) + 300)};
            playerEloMap.put(player, diapason);

            player.sendKeyMessage("messages.queue.elo-changes",
                    "<elo1>", playerEloMap.get(player)[0], "<elo2>", playerEloMap.get(player)[1]);

            int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(LoungePractice.instance, () -> {
                int[] newDiapason;

                if ((playerEloMap.get(player)[0] - 50) < 0) {
                    newDiapason = new int[]{
                            0,
                            playerEloMap.get(player)[1] + 50
                    };
                } else {
                    newDiapason = new int[]{
                            playerEloMap.get(player)[0] - 50,
                            playerEloMap.get(player)[1] + 50
                    };
                }


                playerEloMap.put(player, newDiapason);

                player.sendKeyMessage("messages.queue.elo-changes",
                        "<elo1>", playerEloMap.get(player)[0], "<elo2>", playerEloMap.get(player)[1]);
            }, 15 * 20L, 15 * 20L);

            playerTaskId.put(player, taskId);
            checkForRankedCompatibility(kit, matchType);

        } else checkForCompatibility(kit, matchType);
    }

    public int getPlayersCountInQueue(Kit kit, MatchType matchType) {
        AtomicInteger count = new AtomicInteger();

        queuePlayerList.forEach(loungePlayer -> {
            if (playerKitMap.get(loungePlayer) == kit && playerMatchTypeMap.get(loungePlayer) == matchType)
                count.getAndIncrement();
        });

        return count.get();
    }

    public void removePlayerFromQueue(LoungePlayer player) {
        if (playerMatchTypeMap.get(player) == MatchType.RANKED) {
            Bukkit.getScheduler().cancelTask(playerTaskId.get(player));
            playerTaskId.remove(player);
        }

        queuePlayerList.remove(player);
        playerKitMap.remove(player);
        playerMatchTypeMap.remove(player);
        playerArenasMap.remove(player);

        player.setStatement(Statement.LOBBY);
        player.sendKeyMessage("messages.queue.removed");
    }

    public synchronized void checkForCompatibility(Kit kit, MatchType matchType) {
        if (queuePlayerList.size() < 2) {
            return;
        }

        LoungePlayer player1 = queuePlayerList.get(0);
        LoungePlayer player2 = queuePlayerList.stream().filter(player ->
                        player != player1 &&
                                Objects.equals(playerKitMap.get(player), playerKitMap.get(player1)) &&
                                Objects.equals(playerMatchTypeMap.get(player), playerMatchTypeMap.get(player1)))
                .findFirst().orElse(null);

        if (player2 != null) {
            List<Arena> finalArena = new ArrayList<>();
            finalArena.addAll(playerArenasMap.get(player1));
            finalArena.addAll(playerArenasMap.get(player2));

            new Match(player1, player2, matchType, kit, finalArena.get(ThreadLocalRandom.current().nextInt(finalArena.size()))).start();

            playerKitMap.remove(player1);
            playerMatchTypeMap.remove(player1);
            queuePlayerList.remove(player1);
            playerArenasMap.remove(player1);

            playerKitMap.remove(player2);
            playerMatchTypeMap.remove(player2);
            queuePlayerList.remove(player2);
            playerArenasMap.remove(player1);

            finalArena.clear();
        }
    }

    public synchronized void checkForRankedCompatibility(Kit kit, MatchType matchType) {
        if (queuePlayerList.size() < 2) {
            return;
        }

        LoungePlayer player1 = queuePlayerList.get(0);
        LoungePlayer player2 = queuePlayerList.stream().filter(player ->
                        player != player1 &&
                                Objects.equals(playerKitMap.get(player), playerKitMap.get(player1)) &&
                                Objects.equals(playerMatchTypeMap.get(player), playerMatchTypeMap.get(player1)) &&
                                playerMatchTypeMap.get(player) == MatchType.RANKED &&
                                Arrays.stream(playerEloMap.get(player)).anyMatch(elo -> elo >= playerEloMap.get(player1)[0] || elo <= playerEloMap.get(player1)[1]) &&
                                Arrays.stream(playerEloMap.get(player1)).anyMatch(elo -> elo >= playerEloMap.get(player)[0] || elo <= playerEloMap.get(player)[1]))
                .findFirst().orElse(null);

        if (player2 != null) {
            List<Arena> finalArena = new ArrayList<>();
            finalArena.addAll(playerArenasMap.get(player1));
            finalArena.addAll(playerArenasMap.get(player2));

            new Match(player1, player2, matchType, kit, finalArena.get(ThreadLocalRandom.current().nextInt(finalArena.size()))).start();

            if (playerTaskId.get(player1) != null) {
                Bukkit.getScheduler().cancelTask(playerTaskId.get(player1));
                playerTaskId.remove(player1);
            }

            if (playerTaskId.get(player2) != null) {
                Bukkit.getScheduler().cancelTask(playerTaskId.get(player2));
                playerTaskId.remove(player2);
            }

            playerKitMap.remove(player1);
            playerMatchTypeMap.remove(player1);
            queuePlayerList.remove(player1);
            playerArenasMap.remove(player1);

            playerKitMap.remove(player2);
            playerMatchTypeMap.remove(player2);
            queuePlayerList.remove(player2);
            playerArenasMap.remove(player2);

            finalArena.clear();
        }
    }
}