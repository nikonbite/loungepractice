package me.nikonbite.whitelist.data;

public class PlayerDataEntry {

    private final String playerName;
    private boolean whitelisted;
    private int tokens;

    public PlayerDataEntry(String playerName, boolean whitelisted, int tokens) {
        this.playerName = playerName;
        this.whitelisted = whitelisted;
        this.tokens = tokens;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isWhitelisted() {
        return whitelisted;
    }

    public void setWhitelisted(boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }
}
