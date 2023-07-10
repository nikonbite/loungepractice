package me.nikonbite.practice.match.party;

import lombok.Getter;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.arena.Arena;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.match.Match;
import me.nikonbite.practice.match.type.MatchType;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.util.ChatUtil;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class PartySystem {

    public static List<PartySystem> partyDuelSystemList = new ArrayList<>();
    private final List<LoungePlayer> requests;
    private long expirationTime;
    private final LoungePlayer executor;
    private final LoungePlayer target;
    private final Kit kit;
    private final List<Arena> arenas;

    public PartySystem(LoungePlayer executor, LoungePlayer target, Kit kit, List<Arena> arenas) {
        this.executor = executor;
        this.target = target;
        this.kit = kit;
        this.arenas = arenas;
        requests = new ArrayList<>();
    }

    public void sendPartyDuelRequest() {
        executor.sendKeyMessage("messages.duel.sent", "<player>", target.getName(), "<kit>", kit.getName());
        executor.closeInventory();

        expirationTime = System.currentTimeMillis() + 25000;

        TextComponent message = new TextComponent(Lang.getString("messages.duel.sent-self",
                "<player>", executor.getName(), "<kit>", kit.getName()));

        message.addExtra("  ");
        message.addExtra(ChatUtil.createClickableText(Lang.getString("constant.accept"), "§aClick to accept", "/duel accept " + executor.getName()));
        message.addExtra("  ");
        message.addExtra(ChatUtil.createClickableText(Lang.getString("constant.decline"), "§cClick to decline", "/duel decline " + executor.getName()));

        target.handle().spigot().sendMessage(message);

        partyDuelSystemList.add(this);
        requests.add(target);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > expirationTime) {
                    removeRequest();
                    cancel();
                }
            }
        }.runTaskTimer(LoungePractice.instance, 0L, 20L);
    }

    public void removeRequest() {
        partyDuelSystemList.remove(this);
        requests.remove(target);
    }

    public void acceptPartyDuelRequest(LoungePlayer executor) {
        if (!requests.contains(target)) {
            target.sendKeyList("messages.duel.requests-empty", "<player>", executor.getName());
            return;
        }

        if (this.executor != executor) {
            target.sendKeyList("messages.duel.requests-empty", "<player>", executor.getName());
            return;
        }

        new Match(executor, target, MatchType.DUEL, kit, arenas.get(new Random().nextInt(arenas.size()))).start();
        partyDuelSystemList.remove(this);
    }

    public void declinePartyDuelRequest(LoungePlayer executor) {
        if (!requests.contains(target)) {
            target.sendKeyList("messages.duel.requests-empty", "<player>", executor.getName());
            return;
        }

        if (this.executor != executor) {
            target.sendKeyList("messages.duel.requests-empty", "<player>", executor.getName());
            return;
        }

        target.sendKeyMessage("messages.duel.declined-self", "<player>", executor.getName());
        executor.sendKeyMessage("messages.duel.declined", "<player>", target.getName());

        removeRequest();
    }

}
