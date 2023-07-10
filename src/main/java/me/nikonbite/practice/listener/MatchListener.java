package me.nikonbite.practice.listener;

import lombok.val;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.match.Match;
import me.nikonbite.practice.match.PartyMatch;
import me.nikonbite.practice.match.type.MatchType;
import me.nikonbite.practice.party.Party;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.practice.user.LoungePlayer;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class MatchListener implements Listener {

    public static final Map<LoungePlayer, Long> cooldowns = new HashMap<>();
    public static final Map<LoungePlayer, Map<String, Object>> savedPlayersInfo = new HashMap<>();

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getPlayer());

            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                return;
            }

            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (cooldowns.containsKey(player)) {
                    long cooldownMillis = cooldowns.get(player);
                    long secondsLeft = ((cooldownMillis / 1000) + 16) - (System.currentTimeMillis() / 1000);
                    long remainingMillis = (cooldownMillis + (16 * 1000)) - System.currentTimeMillis();
                    int remainingSeconds = (int) (remainingMillis / 1000);
                    int remainingMilliseconds = (int) (remainingMillis % 1000);
                    String secondsLeftFormat = String.format("%d.%1d", remainingSeconds-16, remainingMilliseconds / 100);


                    if (secondsLeft > 0) {
                        event.setCancelled(true);
                        player.sendKeyMessage("messages.match.enderpearl-cannot-use", "<seconds>", secondsLeftFormat);
                        return;
                    }
                }

                cooldowns.put(player, System.currentTimeMillis() + 16 * 1000);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cooldowns.remove(player);
                    }
                }.runTaskLater(LoungePractice.instance, 16 * 20);
            }
        }
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();

        LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getEntity().getPlayer());

        if (player.getMatch() != null) {
            Match match = player.getMatch();

            LoungePlayer player1 = match.getPlayer1();
            LoungePlayer player2 = match.getPlayer2();

            if (player == player1) {
                MatchListener.updatePlayerInfo(player1, 0);
                MatchListener.updatePlayerInfo(player2, player2.getHealth());
                MatchListener.cooldowns.remove(player1);
                MatchListener.cooldowns.remove(player2);
                match.endMatch(player2, player1);
            } else if (player == player2) {
                MatchListener.updatePlayerInfo(player1, player1.getHealth());
                MatchListener.updatePlayerInfo(player2, 0);
                MatchListener.cooldowns.remove(player1);
                MatchListener.cooldowns.remove(player2);
                match.endMatch(player1, player2);
            }
        }

        if (player.getPartyMatch() != null) {
            PartyMatch partyMatch = player.getPartyMatch();

            partyMatch.getTeam1().forEach(loungePlayer -> {
                if (loungePlayer == LoungePlayer.of(event.getEntity().getPlayer())) {
                    MatchListener.updatePlayerInfo(loungePlayer, loungePlayer.getHealth());
                    MatchListener.cooldowns.remove(loungePlayer);
                }
            });

            if (partyMatch.getMatchType() != MatchType.PARTY_FFA) {
                partyMatch.getTeam2().forEach(loungePlayer -> {
                    if (loungePlayer == LoungePlayer.of(event.getEntity().getPlayer())) {
                        MatchListener.updatePlayerInfo(loungePlayer, loungePlayer.getHealth());
                        MatchListener.cooldowns.remove(loungePlayer);
                    }
                });
            }

            if (partyMatch.getMatchType() != MatchType.PARTY_FFA) {
                if (partyMatch.getTeam1().size() == 0) {
                    partyMatch.endMatch(partyMatch.getTeam2(), partyMatch.getTeam1());
                } else {
                    partyMatch.endMatch(partyMatch.getTeam1(), partyMatch.getTeam1());
                }
            } else {
                if (partyMatch.getTeam1().size() == 1) {
                    partyMatch.endMatch(partyMatch.getTeam1().get(0));
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(event.getPlayer());

        if (loungePlayer.getParty() != null && loungePlayer.getParty().getOwner() == loungePlayer) {
            Party.parties.get(loungePlayer).delete();
        } else if (loungePlayer.getParty() != null) {
            loungePlayer.getParty().memberLeave(loungePlayer);
        }

        if (loungePlayer.getMatch() != null) {
            Match match = loungePlayer.getMatch();

            LoungePlayer player1 = match.getPlayer1();
            LoungePlayer player2 = match.getPlayer2();

            if (loungePlayer == player1) {
                MatchListener.updatePlayerInfo(player1, 0);
                MatchListener.updatePlayerInfo(player2, player2.getHealth());
                MatchListener.cooldowns.remove(player1);
                MatchListener.cooldowns.remove(player2);
                match.endMatch(player2, player1);
            } else if (loungePlayer == player2) {
                MatchListener.updatePlayerInfo(player1, player1.getHealth());
                MatchListener.updatePlayerInfo(player2, 0);
                MatchListener.cooldowns.remove(player1);
                MatchListener.cooldowns.remove(player2);
                match.endMatch(player1, player2);
            }
        }

        if (loungePlayer.getPartyMatch() != null) {
            PartyMatch partyMatch = loungePlayer.getPartyMatch();

            partyMatch.getTeam1().forEach(loungePlayer1 -> {
                if (loungePlayer1 == LoungePlayer.of(event.getPlayer())) {
                    MatchListener.updatePlayerInfo(loungePlayer1, loungePlayer1.getHealth());
                    MatchListener.cooldowns.remove(loungePlayer1);
                }
            });

            if (partyMatch.getMatchType() != MatchType.PARTY_FFA) {
                partyMatch.getTeam2().forEach(loungePlayer1 -> {
                    if (loungePlayer1 == LoungePlayer.of(event.getPlayer())) {
                        MatchListener.updatePlayerInfo(loungePlayer1, loungePlayer1.getHealth());
                        MatchListener.cooldowns.remove(loungePlayer1);
                    }
                });
            }

            if (partyMatch.getMatchType() != MatchType.PARTY_FFA) {
                if (partyMatch.getTeam1().size() == 0) {
                    partyMatch.endMatch(partyMatch.getTeam2(), partyMatch.getTeam1());
                } else {
                    partyMatch.endMatch(partyMatch.getTeam1(), partyMatch.getTeam1());
                }
            } else {
                if (partyMatch.getTeam1().size() == 1) {
                    partyMatch.endMatch(partyMatch.getTeam1().get(0));
                }
            }
        }

        loungePlayer.setStatement(Statement.LOBBY);
        LoungePractice.instance.getQueueSystem().removePlayerFromQueue(loungePlayer);
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getPlayer());

        val drop = event.getItemDrop();

        new BukkitRunnable() {
            int secondsLeft = 10;

            @Override
            public void run() {
                if (secondsLeft <= 0) {
                    if (drop != null)
                        drop.remove();
                    cancel();
                }

                secondsLeft--;
            }
        }.runTaskTimer(LoungePractice.instance, 0, 20);

        if (player.getInventory().getHeldItemSlot() == 0) {
            if (player.getStatement() == Statement.MATCH || player.getStatement() == Statement.FFA || player.getStatement() == Statement.PARTY_MATCH) {
                player.sendKeyMessage("messages.match.first-slot");
                event.setCancelled(true);
                return;
            }
        }

        if (event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE)
            event.getItemDrop().remove();
    }

    public static void updatePlayerInfo(LoungePlayer player, double health) {
        Map<String, Object> playerData = new HashMap<>();

        ItemStack[] armorContents = Arrays.stream(player.getInventory().getArmorContents())
                .sequential()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.reverse(list);
                            return list.toArray(new ItemStack[0]);
                        }));

        /// Сохраняем инвентарь игрока
        playerData.put("inventory", player.getInventory().getContents());
        playerData.put("armor", armorContents);

        /// Сохраняем ник игрока
        playerData.put("playerName", player.getName());

        /// Сохраняем здоровье игрока
        playerData.put("health", health);

        /// Сохраняем голод игрока
        playerData.put("foodLevel", player.getFoodLevel());

        /// Сохраняем эффекты игрока в массиве строк
        List<String> potionEffects = new ArrayList<>();

        for (PotionEffect effect : player.getActivePotionEffects()) {
            String effectName = effect.getType().getName().toLowerCase().replace("_", " ");
            effectName = effectName.substring(0, 1).toUpperCase() + effectName.substring(1);

            int effectDurationSeconds = effect.getDuration() / 20; // конвертируем в секунды
            String effectDuration = String.format("%02d:%02d", effectDurationSeconds / 60, effectDurationSeconds % 60);

            String effectString = "&e" + effectName + " &7- &e" + effectDuration;
            potionEffects.add(effectString);
        }

        playerData.put("potionEffects", potionEffects.toArray(new String[0]));

        /// Проверки и всё гавно
        if (savedPlayersInfo.get(player) != null) {
            savedPlayersInfo.get(player).clear();
            savedPlayersInfo.put(player, playerData);
        } else {
            savedPlayersInfo.put(player, playerData);
        }
    }
}