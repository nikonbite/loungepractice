package me.nikonbite.practice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import me.nikonbite.practice.arena.Arena;
import me.nikonbite.practice.kit.editor.EditorSession;
import me.nikonbite.practice.match.queue.QueueSystem;
import me.nikonbite.api.lib.command.manager.CommandManager;
import me.nikonbite.api.lib.file.FileManager;
import me.nikonbite.api.lib.file.Lang;
import me.nikonbite.api.lib.mysql.HikariConnectionImpl;
import me.nikonbite.api.lib.util.RegistrationUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@RequiredArgsConstructor
public final class LoungePractice extends JavaPlugin {

    public static LoungePractice instance;
    @Getter private QueueSystem queueSystem;
    @Getter private EditorSession editorSession;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Commands & Listeners registration...");

        RegistrationUtil.registerListeners(this, "me.nikonbite.practice.listener");
        CommandManager.registerCommands("me.nikonbite.practice.command");

        Arena.loadArenas();

        queueSystem = new QueueSystem();
        editorSession = new EditorSession();
    }
}