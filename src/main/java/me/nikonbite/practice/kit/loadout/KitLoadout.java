package me.nikonbite.practice.kit.loadout;

import lombok.Getter;
import me.nikonbite.practice.kit.Kit;
import me.nikonbite.practice.user.LoungePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class KitLoadout {

    private final LoungePlayer player;
    private final List<ItemStack[]> NoDebuff;
    private final List<ItemStack[]> Debuff;
    private final List<ItemStack[]> Axe;
    private final List<ItemStack[]> Vanilla;
    private final List<ItemStack[]> Gapple;

    public KitLoadout(LoungePlayer player) {
        this.player = player;

        NoDebuff = new ArrayList<>();
        Debuff = new ArrayList<>();
        Axe = new ArrayList<>();
        Vanilla = new ArrayList<>();
        Gapple = new ArrayList<>();
    }

    public ItemStack[] getCustomKitById(Kit kit, int kitId) {
        switch (kit) {
            case NODEBUFF:
                return NoDebuff.get(kitId);
            case DEBUFF:
                return Debuff.get(kitId);
            case VANILLA:
                return Vanilla.get(kitId);
            case AXE:
                return Axe.get(kitId);
            case GAPPLE:
                return Gapple.get(kitId);
            default:
                return new ItemStack[0];
        }
    }

    public List<ItemStack[]> getCustomKit(Kit kit) {
        switch (kit) {
            case NODEBUFF:
                return NoDebuff;
            case DEBUFF:
                return Debuff;
            case VANILLA:
                return Vanilla;
            case AXE:
                return Axe;
            case GAPPLE:
                return Gapple;
            default:
                return new ArrayList<>();
        }
    }

    public void setCustomKit(Kit kit, List<ItemStack[]> items) {
        switch (kit) {
            case NODEBUFF:
                NoDebuff.addAll(items);
                break;
            case DEBUFF:
                Debuff.addAll(items);
                break;
            case VANILLA:
                Vanilla.addAll(items);
                break;
            case AXE:
                Axe.addAll(items);
                break;
            case GAPPLE:
                Gapple.addAll(items);
                break;
        }
    }

    public boolean isEmpty() {
        return NoDebuff.isEmpty() && Debuff.isEmpty() && Vanilla.isEmpty() && Axe.isEmpty() && Gapple.isEmpty();
    }
}
