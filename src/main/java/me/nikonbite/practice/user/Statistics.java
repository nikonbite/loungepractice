package me.nikonbite.practice.user;

import lombok.Getter;
import me.nikonbite.practice.kit.Kit;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Statistics {

    private final LoungePlayer player;
    private final Map<Kit, Integer> eloMap;
    private final Map<Kit, Integer> winsMap;
    private final Map<Kit, Integer> lossesMap;

    public Statistics(LoungePlayer player) {
        this.player = player;
        this.eloMap = new HashMap<>();
        this.winsMap = new HashMap<>();
        this.lossesMap = new HashMap<>();
    }

    /// ELO
    public int getElo(Kit kit) {
        return eloMap.get(kit);
    }

    public int getGlobalElo() {
        int totalElo = 0;
        int numModes = 0;

        for (Kit kit : eloMap.keySet()) {
            totalElo += eloMap.get(kit);
            numModes++;
        }

        return totalElo / numModes;
    }

    public void addElo(int eloToAdd, Kit kit) {
        int newElo = eloMap.get(kit) + eloToAdd;

        eloMap.put(kit, newElo);
    }

    public void removeElo(int eloToRemove, Kit kit) {
        if (eloMap.get(kit) == 0) return;

        int newElo = eloMap.get(kit) - eloToRemove;

        eloMap.put(kit, newElo);
    }

    /// Wins
    public int getWins(Kit kit) {
        return winsMap.get(kit);
    }

    public int getGlobalWins() {
        int totalWins = 0;

        for (Kit kit : winsMap.keySet()) {
            totalWins += winsMap.get(kit);
        }

        return totalWins;
    }

    public void addWin(Kit kit) {
        int newWins = winsMap.get(kit) + 1;

        winsMap.put(kit, newWins);
    }

    /// Losses
    public int getLosses(Kit kit) {
        return lossesMap.get(kit);
    }

    public int getGlobalLosses() {
        int totalLossess = 0;

        for (Kit kit : lossesMap.keySet()) {
            totalLossess += lossesMap.get(kit);
        }

        return totalLossess;
    }

    public void addLoss(Kit kit) {
        int newLosses = lossesMap.get(kit) + 1;

        lossesMap.put(kit, newLosses);
    }

    public boolean isEmpty() {
        return eloMap.isEmpty() && winsMap.isEmpty() && lossesMap.isEmpty();
    }
}
