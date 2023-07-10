package me.nikonbite.practice.listener;

import lombok.val;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.kit.KitManager;
import me.nikonbite.api.statement.Statement;
import me.nikonbite.practice.user.LoungePlayer;
import me.nikonbite.api.lib.util.ChatUtil;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainListener implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        LoungePlayer loungePlayer = (LoungePlayer) LoungePlayer.of(event.getPlayer());

        val kitLoadout = KitManager.getPlayerKits(loungePlayer.getId()).isEmpty() ? KitManager.insertKits(loungePlayer) : KitManager.getPlayerKits(loungePlayer.getId());
        loungePlayer.setKitLoadout(kitLoadout);

        val statistics = loungePlayer.getStatistics().isEmpty() ? loungePlayer.insertStatistics() : loungePlayer.getStatistics();
        loungePlayer.setStatistics(statistics);

        event.setJoinMessage(null);

        loungePlayer.sendToLobby();
        loungePlayer.setStatement(Statement.LOBBY);
        loungePlayer.setHealth(20);
        loungePlayer.setFoodLevel(20);
        loungePlayer.setGameMode(GameMode.ADVENTURE);
        loungePlayer.setWalkSpeed(0.2F);

        loungePlayer.removeEffects();

        if (loungePlayer.getWorld() == Bukkit.getWorld("world")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(loungePlayer);
                loungePlayer.hidePlayer(player);
            }
        }
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getPlayer());
        event.setFormat(ChatUtil.colorize(
                        player.getGroup().getPrefix() + " " + player.getName() + "&7:&f " + event.getMessage()
                )
        );
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        /// == Убираем окно смерти == ///
        LoungePlayer player = (LoungePlayer) LoungePlayer.of(event.getEntity().getPlayer());

        LoungePractice.instance.getServer().getScheduler().scheduleSyncDelayedTask(LoungePractice.instance, () -> {
            if (player.isDead())
                player.getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
        });
        ///  == == == == == == == ==  ///
    }
}