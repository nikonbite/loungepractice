package me.nikonbite.lobby.board;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.SneakyThrows;
import me.nikonbite.lobby.LoungeLobby;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class LoungeBoard {
    private final Map<Player, Scoreboard> scoreboards;

    public LoungeBoard() {
        scoreboards = new HashMap<>();
        Bukkit.getScheduler().runTaskTimerAsynchronously(LoungeLobby.getProvidingPlugin(LoungeLobby.class), this::updateScoreboard, 0L, 100L);
    }

    public void setScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("PvPLounge", "dummy");
        objective.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "PvPLounge");

        Score practiceScore = objective.getScore(ChatColor.RED + "Practice");
        practiceScore.setScore(getTotalOnline());

        Score lobbyScore = objective.getScore(ChatColor.YELLOW + "Lobby");
        lobbyScore.setScore(getServerOnline(player));

        player.setScoreboard(scoreboard);
        scoreboards.put(player, scoreboard);
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        scoreboards.remove(player);
    }

    private int getTotalOnline() {
        int[] online = {0, 0}; // Используем массив для сохранения онлайна в двух регионах
        CountDownLatch latch = new CountDownLatch(2); // Используем счетчик, чтобы дождаться обоих ответов

        // Отправляем запрос на BungeeCord для каждого региона
        for (int i = 0; i < 2; i++) {
            String region = (i == 0) ? "eu-practice" : "na-practice";
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("PlayerCount");
                out.writeUTF(region);

                int finalI = i;
                sendBungeePacket(b.toByteArray(), response -> {
                    online[finalI] = Integer.parseInt(response);
                    latch.countDown();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Дожидаемся обоих ответов и возвращаем сумму онлайна
        try {
            latch.await(); // Ожидаем, пока оба ответа не будут получены
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return online[0] + online[1];
    }


    @SneakyThrows
    private void sendBungeePacket(byte[] message, Consumer<String> callback) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        out.writeShort(message.length);
        out.write(message);

        Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

        assert player != null;
        player.sendPluginMessage(LoungeLobby.getProvidingPlugin(LoungeLobby.class), "BungeeCord", b.toByteArray());

        Bukkit.getScheduler().runTaskAsynchronously(LoungeLobby.getProvidingPlugin(LoungeLobby.class), () -> {
            byte[] buffer = new byte[Short.MAX_VALUE];
            Player player1 = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);

            // Ждем ответа от BungeeCord
            Bukkit.getServer().getMessenger().registerIncomingPluginChannel(LoungeLobby.getProvidingPlugin(LoungeLobby.class), "BungeeCord", (channel, player11, message1) -> {
                if (!channel.equals("BungeeCord")) {
                    return;
                }

                ByteArrayInputStream b1 = new ByteArrayInputStream(message1);
                DataInputStream in = new DataInputStream(b1);

                try {
                    String subChannel = in.readUTF();
                    if (subChannel.equals("LoungeLobbyChannel")) {
                        in.readFully(buffer, 0, in.readShort());
                        callback.accept(new String(buffer));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Отправляем пакет
            player1.sendPluginMessage(LoungeLobby.getProvidingPlugin(LoungeLobby.class), "BungeeCord", "LoungeLobbyChannel".getBytes());

            // Ждем ответа 5 секунд
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Удаляем слушателя, чтобы избежать утечек памяти
            Bukkit.getServer().getMessenger().unregisterIncomingPluginChannel(LoungeLobby.getProvidingPlugin(LoungeLobby.class), "BungeeCord");
        });
    }


    private int getServerOnline(Player player) {
        return player.getServer().getOnlinePlayers().size();
    }

    private void updateScoreboard() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (scoreboards.containsKey(player)) {
                Scoreboard scoreboard = scoreboards.get(player);
                Objective objective = scoreboard.getObjective("PvPLounge");
                if (objective != null) {
                    Score practiceScore = objective.getScore(ChatColor.RED + "Practice");
                    practiceScore.setScore(getTotalOnline());

                    Score lobbyScore = objective.getScore(ChatColor.YELLOW + "Lobby");
                    lobbyScore.setScore(getServerOnline(player));
                }
            }
        }
    }
}