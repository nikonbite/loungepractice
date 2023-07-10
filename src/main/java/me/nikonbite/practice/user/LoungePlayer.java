package me.nikonbite.practice.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import me.nikonbite.api.LoungeAPI;
import me.nikonbite.api.lib.file.Config;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.user.Gamer;
import me.nikonbite.api.lib.user.group.Group;
import me.nikonbite.api.lib.user.registry.GamerRegistry;
import me.nikonbite.api.lib.util.LocationUtil;
import me.nikonbite.practice.LoungePractice;
import me.nikonbite.practice.hotbar.HotbarManager;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.kit.KitManager;
import me.nikonbite.practice.kit.loadout.KitLoadout;
import me.nikonbite.practice.match.Match;
import me.nikonbite.practice.match.PartyMatch;
import me.nikonbite.practice.party.Party;
import me.nikonbite.api.statement.Statement;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class LoungePlayer extends Gamer {

    private Statement statement = Statement.LOBBY;
    private KitLoadout kitLoadout;
    private Statistics statistics;
    private Match match;
    private PartyMatch partyMatch;
    private Party party;

    public LoungePlayer(int id) {
        super(id);
    }

    public void sendKeyMessage(@NonNull String key, Object... args) {
        sendMessage(Lang.getString(key, args));
    }

    public void sendKeyList(@NonNull String key, Object... args) {
        Lang.getList(key, args).forEach(this::sendMessage);
    }

    public void setStatement(Statement statement) {
        this.statement = statement;

        setHotbarByState();
    }

    public void setHotbarByState() {
        if (party != null) {
            clearFullInventory();
            fillFirstInventoryLine(
                    HotbarManager.PARTY_EVENTS_ITEM,
                    HotbarManager.PARTY_MEMBERS_ITEM,
                    HotbarManager.PARTY_FIGHT_OTHER_ITEM,
                    null, null, null, null, null,
                    HotbarManager.PARTY_LEAVE_ITEM
            );
        }

        switch (this.getStatement()) {
            case LOBBY:
                clearFullInventory();
                getHandle().getDataWatcher().watch(9, (byte) 0);
                setFireTicks(0);
                setHealth(20);
                setFoodLevel(20);
                setAllowFlight(false);
                setFlying(false);

                this.fillFirstInventoryLine(
                        HotbarManager.KIT_EDITOR_ITEM,
                        null, null,
                        HotbarManager.PARTY_CREATE_ITEM,
                        HotbarManager.SPECTATOR_ITEM,
                        null,
                        HotbarManager.FFA_ITEM,
                        HotbarManager.UNRANKED_ITEM,
                        HotbarManager.RANKED_ITEM
                );
                break;
            case QUEUE:
                clearFullInventory();
                fillFirstInventoryLine(HotbarManager.QUEUE_LEAVE_ITEM,
                        null, null, null, null, null, null, null, null);
                break;
            case PARTY:
                clearFullInventory();
                fillFirstInventoryLine(
                        HotbarManager.PARTY_EVENTS_ITEM,
                        HotbarManager.PARTY_FIGHT_OTHER_ITEM,
                        HotbarManager.PARTY_MEMBERS_ITEM,
                        null, null, null, null, null,
                        HotbarManager.PARTY_LEAVE_ITEM
                );
                break;
            case SPECTATOR:
                clearFullInventory();
                fillFirstInventoryLine(
                        HotbarManager.SPECTATOR_TELEPORTER_ITEM,
                        HotbarManager.SPECTATOR_LIST_ITEM,
                        HotbarManager.SPECTATOR_FFA_ITEM,
                        null, null, null, null, null,
                        HotbarManager.SPECTATOR_LEAVE_ITEM);
        }
    }


    public void removeEffects() {
        handle().getActivePotionEffects().forEach(potionEffect -> handle().removePotionEffect(potionEffect.getType()));
    }

    public void removeDrops() {
        World world = Bukkit.getWorld("maps");
        List<Entity> entities = world.getEntities();
        for (Entity entity : entities) {
            if (entity instanceof Item && ((Item) entity).getItemStack().getType() != Material.AIR) {
                entity.remove();
            }
        }
    }

    public boolean hasGroup(Group group, boolean sendMessage) {
        if (this.getGroup().ordinal() >= group.ordinal()) return true;
        else {
            if (sendMessage) this.sendMessage("No perms!");
            return false;
        }
    }

    public void sendToFfa() {
        if (statement == Statement.FFA) {
            sendKeyMessage("messages.ffa.already-in");
            return;
        }

        if (statement == Statement.MATCH || statement == Statement.PARTY_MATCH || statement == Statement.PARTY) {
            sendKeyMessage("messages.ffa.join-error");
            return;
        }

        teleport(LocationUtil.stringToLocation(Config.getString("spawn-locations.ffa")));
        KitManager.giveKit(this, Kit.NODEBUFF);
        setStatement(Statement.FFA);
        sendKeyMessage("messages.ffa.sent");
    }

    public void sendToFfaAsSpectator() {
        GamerRegistry.getUSER_MAP().values().forEach(player1 -> {
            if (player1.getWorld() == Bukkit.getWorld("ffa")) {
                PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, getHandle());
                player1.sendPacket(packet);
            }
        });

        setAllowFlight(true);
        setFlying(true);
        setGameMode(GameMode.ADVENTURE);
        teleport(LocationUtil.stringToLocation(Config.getString("spawn-locations.ffa")));
        sendKeyMessage("messages.ffa.sent-as-spectator");
    }

    public void sendToLobby() {
        teleport(LocationUtil.stringToLocation(Config.getString("spawn-locations.lobby")));
    }

    public void sendToEditor(Kit kit) {
        setStatement(Statement.EDITOR);
        teleport(LocationUtil.stringToLocation(Config.getString("spawn-locations.editor")));
        this.getInventory().setContents(kit.getContents().values().toArray(new ItemStack[0]));
        LoungePractice.instance.getEditorSession().addToSession(this, kit);
    }

    public void removeFromFfa() {
        if (statement != Statement.FFA) {
            sendKeyMessage("messages.ffa.not-in");
            return;
        }

        teleport(LocationUtil.stringToLocation(Config.getString("spawn-locations.lobby")));
        setStatement(Statement.LOBBY);
        sendKeyMessage("messages.ffa.leave");
    }


    public void fillInventory(ItemStack... items) {
        this.getInventory().setContents(items);
    }

    public void clearFullInventory() {
        this.getInventory().clear();
        this.removeArmor();
    }

    public void fillFirstInventoryLine(ItemStack... items) {
        for (int i = 0; i < 9; i++) {
            if (items.length > i || items[0] != null)
                this.getInventory().setItem(i, items[i]);
        }
    }

    /**
     * 1 - Шлем <br>
     * 2 - Нагрудник <br>
     * 3 - Штаны <br>
     * 4 - Ботинки <br>
     */
    public void fillArmor(ItemStack... items) {
        this.getInventory().setArmorContents(items);
    }

    public void removeArmor() {
        this.getInventory().setArmorContents(null);
    }

    public Statistics getStatistics() {
        Statistics statistics = new Statistics(this);

        LoungeAPI.hikariConnection.executeQuery("SELECT * FROM `Statistics` WHERE `Id` = ?", rs -> {
            if (rs.next()) {
                Kit[] kits = Kit.values();
                for (Kit kit : kits) {
                    String json = rs.getString(kit.name());
                    if (json != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        TypeReference<Map<String, Integer>> typeRef = new TypeReference<Map<String, Integer>>() {
                        };
                        Map<String, Integer> jsonMap = mapper.readValue(json, typeRef);
                        int elo = jsonMap.getOrDefault("elo", 1000);
                        int wins = jsonMap.getOrDefault("wins", 0);
                        int losses = jsonMap.getOrDefault("losses", 0);

                        statistics.getEloMap().put(kit, elo);
                        statistics.getWinsMap().put(kit, wins);
                        statistics.getLossesMap().put(kit, losses);
                    }
                }
            }

            return null;
        }, getId());

        return statistics;
    }

    void saveStatistics(Statistics statistics) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE `Statistics` SET ");
        Kit[] kits = Kit.values();
        for (int i = 0; i < kits.length; i++) {
            queryBuilder.append(kits[i].name()).append("=?");
            if (i < kits.length - 1) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(" WHERE `Id`=?");

        Object[] params = new Object[kits.length + 1];
        for (int i = 0; i < kits.length; i++) {
            Kit kit = kits[i];

            int elo = statistics.getElo(kit);
            int wins = statistics.getWins(kit);
            int losses = statistics.getLosses(kit);

            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("elo", elo);
            objectNode.put("wins", wins);
            objectNode.put("losses", losses);
            String json = objectNode.toString();

            params[i] = json;
        }
        params[kits.length] = getId();

        LoungeAPI.hikariConnection.execute(queryBuilder.toString(), params);
    }

    public Statistics insertStatistics() {
        Statistics statistics = new Statistics(this);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jsonMap = new HashMap<>();

        for (Kit kit : Kit.values()) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put("elo", 1000);
            objectNode.put("wins", 0);
            objectNode.put("losses", 0);
            String json = objectNode.toString();
            jsonMap.put(kit.name(), json);
        }

        LoungeAPI.hikariConnection.execute("INSERT INTO `Statistics` VALUES (?, ?, ?, ?, ?, ?, ?)",
                getId(), getName(), jsonMap.get(Kit.NODEBUFF.name()), jsonMap.get(Kit.DEBUFF.name()),
                jsonMap.get(Kit.VANILLA.name()), jsonMap.get(Kit.AXE.name()), jsonMap.get(Kit.GAPPLE.name()));

        for (Kit kit : Kit.values()) {
            statistics.getEloMap().put(kit, 1000);
            statistics.getWinsMap().put(kit, 0);
            statistics.getLossesMap().put(kit, 0);
        }

        return statistics;
    }

    public void save() {
        val connection = LoungeAPI.hikariConnection;
        KitManager.savePlayerKits(getId(), kitLoadout);
        saveStatistics(statistics);
    }
}