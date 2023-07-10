package me.nikonbite.lobby;

import lombok.Getter;
import me.nikonbite.lobby.board.LoungeBoard;
import me.nikonbite.lobby.listener.MainListener;
import org.bukkit.plugin.java.JavaPlugin;

public class LoungeLobby extends JavaPlugin {

    @Getter
    private static LoungeLobby instance;
    @Getter
    private LoungeBoard loungeBoard;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        loungeBoard = new LoungeBoard();

        getServer().getPluginManager().registerEvents(new MainListener(), this);
    }
}