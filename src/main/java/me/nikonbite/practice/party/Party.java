package me.nikonbite.practice.party;

import lombok.Getter;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.util.ChatUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class Party {
    private final List<LoungePlayer> playerList;
    private final LoungePlayer owner;
    private final int maxCount = 40;
    private final List<LoungePlayer> requests;
    private long expirationTime;
    public static Map<LoungePlayer, Party> parties = new HashMap<>();


    public Party(LoungePlayer owner) {
        playerList = new ArrayList<>();
        requests = new ArrayList<>();
        this.owner = owner;

        parties.put(owner, this);
        playerList.add(owner);

        owner.setStatement(Statement.PARTY);
        owner.setParty(this);
        owner.sendKeyList("messages.party.created");
    }

    public void sendRequest(LoungePlayer player) {
        synchronized (playerList) {
            if (playerList.size() + 1 == maxCount) {
                owner.sendKeyMessage("messages.party.party-full");
                return;
            }

            if (player.equals(owner)) {
                owner.sendKeyMessage("messages.party.cant-invite-yourself");
                return;
            }

            if (playerList.contains(player)) {
                owner.sendKeyMessage("messages.party.already-in-party");
                return;
            }

            owner.sendKeyMessage("messages.party.invite-sent-self", "<player>", player.getName());

            expirationTime = System.currentTimeMillis() + 25000;

            TextComponent message = new TextComponent(Lang.getString("messages.party.invite-sent", "<player>", owner.getName()) + " ");
            message.addExtra(ChatUtil.createClickableText(Lang.getString("constant.accept"), "§aClick to accept", "/party accept " + owner.getName()));
            message.addExtra("     ");
            message.addExtra(ChatUtil.createClickableText(Lang.getString("constant.decline"), "§cClick to decline","/party decline " + owner.getName()));

            player.handle().spigot().sendMessage(message);

            requests.add(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    synchronized (playerList) {
                        if (System.currentTimeMillis() > expirationTime) {
                            requests.remove(player);
                            cancel();
                        } else if (playerList.contains(player)) {
                            requests.remove(player);
                            cancel();
                        }
                    }
                }
            }.runTaskTimer(LoungePractice.instance, 0L, 20L);
        }
    }


    public void addMember(LoungePlayer player) {
        if (player == null || !player.isOnline()) {
            owner.sendKeyMessage("messages.party.player-not-found");
            return;
        }

        playerList.add(player);
        player.setStatement(Statement.PARTY);
        player.setParty(this);

        player.sendKeyMessage("messages.party.player-accepted-self", "<player>", owner.getName());
        owner.sendKeyMessage("messages.party.player-accepted", "<player>", player.getName());
    }

    public void removeMember(LoungePlayer player) {
        if (!playerList.contains(player)) {
            owner.sendKeyMessage("messages.party.player-not-in-party");
            return;
        }

        if (player == owner) {
            owner.sendKeyMessage("messages.party.cant-remove-yourself");
            return;
        }

        playerList.remove(player);
        player.setStatement(Statement.LOBBY);
        player.setParty(null);

        owner.sendKeyMessage("messages.party.player-removed-self", "<player>", player.getName());
        player.sendKeyMessage("messages.party.player-removed");
    }

    public void memberLeave(LoungePlayer player) {
        playerList.remove(player);
        player.setStatement(Statement.LOBBY);
        player.setParty(null);

        owner.sendKeyMessage("messages.party.player-leaved-self", "<player>", player.getName());
        player.sendKeyMessage("messages.party.player-removed");
    }

    public void delete() {
        owner.sendKeyMessage("messages.party.disbanded");

        Iterator<LoungePlayer> iterator = playerList.iterator();
        while (iterator.hasNext()) {
            LoungePlayer playerToRemove = iterator.next();
            if (playerToRemove != owner)
                playerToRemove.sendKeyMessage("messages.party.player-removed");

            iterator.remove();
            playerToRemove.setParty(null);
            playerToRemove.setStatement(Statement.LOBBY);
        }

        parties.remove(owner);
    }

    public void sendMemberList(LoungePlayer loungePlayer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playerList.size(); i++) {
            sb.append(ChatUtil.colorize(playerList.get(i).getGroup().getColor() + playerList.get(i).getName()));
            if (i != playerList.size() - 1) sb.append(", ");
        }
        String members = sb.toString();

        loungePlayer.sendKeyList("messages.party.list",
                "<owner>", owner.getName(),
                      "<count>", String.valueOf(playerList.size()),
                      "<members>", ChatUtil.colorize(String.join("&7,&r ", members))
        );
    }
}
