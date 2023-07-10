package me.nikonbite.api.lib.user;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.*;
import me.nikonbite.api.LoungeAPI;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.user.group.Group;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.user.wrapper.DataWrapper;
import me.nikonbite.api.starter.bukkit.BukkitAPIStarter;
import me.nikonbite.api.statement.Statement;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

@Getter
@Setter
public class Gamer extends CraftPlayer {

    private final int id;
    private String name;
    private Group group;
    private Statement statement = Statement.LOBBY;
    private boolean freezed = false;

    public Gamer(@NonNull CraftServer server, @NonNull EntityPlayer entity, int id) {
        super(server, entity);

        this.id = id;

        load();
    }

    public Gamer(@NonNull Player player, int id) {
        this((CraftServer) player.getServer(), ((CraftPlayer) player).getHandle(), id);
    }

    public Gamer(int id) {
        this(Objects.requireNonNull(Bukkit.getPlayerExact(DataWrapper.getName(id))), id);
    }

    private void load() {
        setName(DataWrapper.getName(id));
        setGroup(DataWrapper.getGroup(id));
    }

    public static Gamer of(@NonNull Player player) {
        return GamerRegistry.get(player);
    }

    public void sendPacket(Packet<?>... packets) {
        for (var packet : packets) {
            getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void sendKeyMessage(@NonNull String key, Object... args) {
        sendMessage(Lang.getString(key, args));
    }

    public void sendKeyList(@NonNull String key, Object... args) {
        Lang.getList(key, args).forEach(this::sendMessage);
    }

    public Player handle() {
        return Bukkit.getPlayerExact(getName());
    }

    public void removeEffects() {
        handle().getActivePotionEffects().forEach(potionEffect -> handle().removePotionEffect(potionEffect.getType()));
    }

    public boolean hasGroup(Group group, boolean sendMessage) {
        if (this.getGroup().ordinal() >= group.ordinal()) return true;
        else {
            if (sendMessage) this.sendMessage("No perms!");
            return false;
        }
    }

    public void freeze() {
        setWalkSpeed(0.0F);
        addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 250, false, false));
        freezed = true;
    }

    public void unfreeze() {
        setWalkSpeed(0.2F);
        removePotionEffect(PotionEffectType.JUMP);
        freezed = false;
    }

    @Override
    public Player.Spigot spigot() {
        return new Player.Spigot();
    }

    public void setGroup(Group group) {
        this.group = group;
        LoungeAPI.hikariConnection.execute("UPDATE `Groups` SET `Group` = ? WHERE `Groups`.`Id` = ?", getGroup().name(), getId());
    }

    public void sendToServer(String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        sendPluginMessage(BukkitAPIStarter.getProvidingPlugin(BukkitAPIStarter.class), "BungeeCord", out.toByteArray());
    }

    @SneakyThrows
    public int getPing() {
        return (int) getHandle().getClass().getField("ping").get(getHandle());
    }
}
