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
import me.nikonbite.practice.util.ConvertationUtil;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class PartyMatch {

    public static List<PartyMatch> matchList = new ArrayList<>();
    private final LoungePractice plugin = LoungePractice.instance;
    public List<LoungePlayer> spectators;

    private List<LoungePlayer> team1;
    private List<LoungePlayer> team2;

    private MatchType matchType;
    private Arena arena;
    private Kit kit;

    private boolean active;
    private boolean waiting;
    private int countdownTaskId;

    public PartyMatch(List<LoungePlayer> team1, MatchType matchType, Kit kit, Arena arena) {
        this.team1 = team1;
        this.matchType = matchType;
        this.kit = kit;
        this.arena = arena;

        spectators = new ArrayList<>();
    }

    public PartyMatch(List<LoungePlayer> team1, List<LoungePlayer> team2, MatchType matchType, Kit kit, Arena arena) {
        this.team1 = team1;
        this.team2 = team2;
        this.matchType = matchType;
        this.kit = kit;
        this.arena = arena;

        spectators = new ArrayList<>();
    }

    public void prepare(LoungePlayer loungePlayer) {
        loungePlayer.setPartyMatch(this);

        loungePlayer.setGameMode(GameMode.SURVIVAL);
        loungePlayer.setStatement(Statement.PARTY_MATCH);
        loungePlayer.removeDrops();

        loungePlayer.setHealth(20);
        loungePlayer.setFoodLevel(20);
        loungePlayer.setSaturation(6);

        loungePlayer.freeze();
        loungePlayer.clearFullInventory();

        // выдача набора
        KitManager.giveKitSelector(loungePlayer, kit);
    }

    public void start() {
        if (active) return;
        active = true;
        matchList.add(this);

        switch (matchType) {
            case PARTY_VS_PARTY:
                team1.forEach(loungePlayer -> {
                    prepare(loungePlayer);

                    loungePlayer.teleport(LocationUtil.stringToLocation(arena.getFirstPlayerSpawn()));
                    loungePlayer.sendKeyMessage("messages.party-match.party-vs-party-opponent", "<player>", team2.get(0).getName(), "<kit>", kit.getName());
                    loungePlayer.sendKeyMessage("messages.match.now-playing", "<map>", arena.getName());
                });

                team2.forEach(loungePlayer -> {
                    prepare(loungePlayer);

                    loungePlayer.teleport(LocationUtil.stringToLocation(arena.getSecondPlayerSpawn()));
                    loungePlayer.sendKeyMessage("messages.party-match.party-vs-party-opponent", "<player>", team1.get(0).getName(), "<kit>", kit.getName());
                    loungePlayer.sendKeyMessage("messages.match.now-playing", "<map>", arena.getName());
                });

                // начало отсчёта времени до игры
                countdownTaskId = new BukkitRunnable() {
                    int secondsLeft = 5;

                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            startGame();
                            waiting = false;

                            team1.forEach(LoungePlayer::unfreeze);
                            team2.forEach(LoungePlayer::unfreeze);
                            cancel();
                            return;
                        }
                        waiting = true;

                        // Отправка сообщения каждую секунду
                        team1.forEach(loungePlayer -> loungePlayer.sendKeyMessage("messages.match.timer", "<seconds>", secondsLeft));
                        team2.forEach(loungePlayer -> loungePlayer.sendKeyMessage("messages.match.timer", "<seconds>", secondsLeft));
                        secondsLeft--;
                    }
                }.runTaskTimer(LoungePractice.instance, 0, 20).getTaskId();
                break;
            case PARTY_SPLIT:
                team1.forEach(loungePlayer -> {
                    prepare(loungePlayer);

                    loungePlayer.teleport(LocationUtil.stringToLocation(arena.getFirstPlayerSpawn()));
                    loungePlayer.sendKeyMessage("messages.party-match.split-opponent", "<player>", ConvertationUtil.formatLoungePlayers(team2), "<kit>", kit.getName());
                    loungePlayer.sendKeyMessage("messages.match.now-playing", "<map>", arena.getName());
                });

                team2.forEach(loungePlayer -> {
                    prepare(loungePlayer);

                    loungePlayer.teleport(LocationUtil.stringToLocation(arena.getSecondPlayerSpawn()));
                    loungePlayer.sendKeyMessage("messages.party-match.split-opponent", "<player>", ConvertationUtil.formatLoungePlayers(team1), "<kit>", kit.getName());
                    loungePlayer.sendKeyMessage("messages.match.now-playing", "<map>", arena.getName());
                });

                // начало отсчёта времени до игры
                countdownTaskId = new BukkitRunnable() {
                    int secondsLeft = 5;

                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            startGame();
                            waiting = false;

                            team1.forEach(LoungePlayer::unfreeze);
                            team2.forEach(LoungePlayer::unfreeze);
                            cancel();
                            return;
                        }
                        waiting = true;

                        // Отправка сообщения каждую секунду
                        team1.forEach(loungePlayer -> loungePlayer.sendKeyMessage("messages.match.timer", "<seconds>", secondsLeft));
                        team2.forEach(loungePlayer -> loungePlayer.sendKeyMessage("messages.match.timer", "<seconds>", secondsLeft));
                        secondsLeft--;
                    }
                }.runTaskTimer(LoungePractice.instance, 0, 20).getTaskId();
                break;
            case PARTY_FFA:
                team1.forEach(loungePlayer -> {
                    prepare(loungePlayer);

                    loungePlayer.teleport(LocationUtil.stringToLocation(arena.getFirstPlayerSpawn()));

                    loungePlayer.sendKeyMessage("messages.party-match.ffa-opponent", "<kit>", kit.getName());
                    loungePlayer.sendKeyMessage("messages.match.now-playing", "<map>", arena.getName());
                });

                // начало отсчёта времени до игры
                countdownTaskId = new BukkitRunnable() {
                    int secondsLeft = 5;

                    @Override
                    public void run() {
                        if (secondsLeft <= 0) {
                            startGame();
                            waiting = false;

                            team1.forEach(LoungePlayer::unfreeze);
                            cancel();
                            return;
                        }
                        waiting = true;

                        // Отправка сообщения каждую секунду
                        team1.forEach(loungePlayer -> loungePlayer.sendKeyMessage("messages.match.timer", "<seconds>", secondsLeft));
                        secondsLeft--;
                    }
                }.runTaskTimer(LoungePractice.instance, 0, 20).getTaskId();
                break;
        }
    }

    private void startGame() {
        if (matchType != MatchType.PARTY_FFA) {
            team1.forEach(loungePlayer -> {
                loungePlayer.sendKeyMessage("messages.match.started");
                loungePlayer.sendKeyMessage("messages.match.reminder");
            });
            team2.forEach(loungePlayer -> {
                loungePlayer.sendKeyMessage("messages.match.started");
                loungePlayer.sendKeyMessage("messages.match.reminder");
            });
        } else {
            team1.forEach(loungePlayer -> {
                loungePlayer.sendKeyMessage("messages.match.started");
                loungePlayer.sendKeyMessage("messages.match.reminder");
            });
        }
    }

    public void addSpectator(LoungePlayer spectator) {
        spectator.setStatement(Statement.SPECTATOR);
        spectator.setGameMode(GameMode.ADVENTURE);
        spectator.setAllowFlight(true);
        spectator.setFlying(true);

        long expirationTimeLoser = System.currentTimeMillis() + 100;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > expirationTimeLoser) {
                    PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, spectator.getHandle());

                    team1.forEach(loungePlayer -> loungePlayer.sendPacket(packet));
                    if (matchType != MatchType.PARTY_FFA) {
                        team2.forEach(loungePlayer -> loungePlayer.sendPacket(packet));
                    }
                    cancel();
                }
            }
        }.runTaskTimer(LoungePractice.instance, 0L, 5L);


        spectator.teleport(LocationUtil.stringToLocation(arena.getSpectatorSpawn()));
        spectators.add(spectator);
    }

    public void removeSpectator(LoungePlayer spectator) {
        spectator.setStatement(Statement.LOBBY);
        spectator.setGameMode(GameMode.ADVENTURE);
        spectator.setAllowFlight(false);
        spectator.setFlying(false);

        spectator.sendToLobby();
        spectators.remove(spectator);
    }

    public void endMatch(List<LoungePlayer> winnerTeam, List<LoungePlayer> loserTeam) {
        Bukkit.getScheduler().cancelTask(countdownTaskId);


        TextComponent message = new TextComponent(Lang.getString("messages.match.inventory-message"));
        message.addExtra(ChatUtil.createClickableText("§a" + winnerTeam.get(0).getName(), "§aClick to view", "/checksavedinv " + winnerTeam.get(0).getName()));
        message.addExtra("§7, ");
        message.addExtra(ChatUtil.createClickableText("§c" + loserTeam.get(0).getName(), "§cClick to view", "/checksavedinv " + loserTeam.get(0).getName()));

        /// Winner
        winnerTeam.forEach(loungePlayer -> {
            loungePlayer.sendKeyMessage("messages.match.winner", "<winner>", winnerTeam.get(0).getName());
            loungePlayer.handle().spigot().sendMessage(message);
            loungePlayer.sendKeyMessage("messages.match.map-rate");

            loungePlayer.setHealth(20);
            loungePlayer.setFoodLevel(20);
            loungePlayer.clearFullInventory();
            loungePlayer.removeEffects();
            loungePlayer.getHandle().getDataWatcher().watch(9, (byte) 0);
            loungePlayer.setFireTicks(0);
            loungePlayer.setGameMode(GameMode.ADVENTURE);
        });

        long expirationTimeWinner = System.currentTimeMillis() + 5000;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > expirationTimeWinner) {

                    winnerTeam.forEach(loungePlayer -> {
                        loungePlayer.setStatement(Statement.PARTY);
                        loungePlayer.sendToLobby();
                        cancel();
                    });
                }
            }
        }.runTaskTimer(LoungePractice.instance, 0L, 20L);

        /// Loser
        loserTeam.forEach(loungePlayer -> {
            loungePlayer.sendKeyMessage("messages.match.winner", "<winner>", winnerTeam.get(0).getName());
            loungePlayer.handle().spigot().sendMessage(message);
            loungePlayer.sendKeyMessage("messages.match.map-rate");

            loungePlayer.setHealth(20);
            loungePlayer.setFoodLevel(20);
            loungePlayer.removeEffects();
            loungePlayer.getHandle().getDataWatcher().watch(9, (byte) 0);
            loungePlayer.setFireTicks(0);
            loungePlayer.setGameMode(GameMode.ADVENTURE);
        });

        long expirationTimeLoser = System.currentTimeMillis() + 100;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > expirationTimeLoser) {
                    loserTeam.forEach(loungePlayer -> {
                        loungePlayer.setStatement(Statement.PARTY);
                        loungePlayer.sendToLobby();
                        cancel();
                    });
                }
            }
        }.runTaskTimer(LoungePractice.instance, 0L, 5L);

        active = false;
        matchList.remove(this);
    }

    public void endMatch(LoungePlayer winner) {
        Bukkit.getScheduler().cancelTask(countdownTaskId);

        TextComponent message = new TextComponent(Lang.getString("messages.match.inventory-message"));
        message.addExtra(ChatUtil.createClickableText("§a" + winner.getName(), "§aClick to view", "/checksavedinv " + winner.getName()));

        long expirationTimeWinner = System.currentTimeMillis() + 5000;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > expirationTimeWinner) {
                    winner.setStatement(Statement.PARTY);
                    winner.sendToLobby();
                    cancel();
                }
            }
        }.runTaskTimer(LoungePractice.instance, 0L, 20L);

        team1.forEach(loungePlayer -> {
            loungePlayer.sendKeyMessage("messages.match.winner", "<winner>", winner.getName());
            loungePlayer.handle().spigot().sendMessage(message);
            loungePlayer.sendKeyMessage("messages.match.map-rate");

            loungePlayer.setHealth(20);
            loungePlayer.setFoodLevel(20);
            loungePlayer.removeEffects();
            loungePlayer.getHandle().getDataWatcher().watch(9, (byte) 0);
            loungePlayer.setFireTicks(0);
            loungePlayer.setGameMode(GameMode.ADVENTURE);
        });

        long expirationTimeLoser = System.currentTimeMillis() + 100;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > expirationTimeLoser) {
                    team1.forEach(loungePlayer -> {
                        if (loungePlayer == winner) return;
                        loungePlayer.setStatement(Statement.PARTY);
                        loungePlayer.sendToLobby();
                        cancel();
                    });
                }
            }
        }.runTaskTimer(LoungePractice.instance, 0L, 5L);

        active = false;
        matchList.remove(this);
    }
}