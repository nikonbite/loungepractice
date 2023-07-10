package me.nikonbite.api.starter.bukkit;

import lombok.Getter;
import me.nikonbite.api.LoungeAPI;
import me.nikonbite.api.lib.command.manager.CommandManager;
import me.nikonbite.api.lib.util.RegistrationUtil;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BukkitAPIStarter extends JavaPlugin {

    @Getter
    private static BukkitAPIStarter instance;
    private LoungeAPI loungeAPI;

    @Override
    public void onEnable() {
        instance = this;

        loungeAPI = new LoungeAPI();
        loungeAPI.init(this);

        /// Регистрируем слушатели автоматически
        RegistrationUtil.registerListeners(this);

        /// Регистрируем команды автоматически
        CommandManager.registerCommands(this);
    }
}
