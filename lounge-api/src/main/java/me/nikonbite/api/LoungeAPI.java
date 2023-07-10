package me.nikonbite.api;

import lombok.Getter;
import lombok.experimental.NonFinal;
import me.nikonbite.api.lib.file.Config;
import me.nikonbite.api.lib.file.FileManager;
import me.nikonbite.api.lib.mysql.HikariConnectionImpl;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class LoungeAPI {

    @Getter private static LoungeAPI instance;
    @NonFinal public static HikariConnectionImpl hikariConnection;
    private FileManager fileManager;

    public void init(Object plugin) {
        /// Регистрируем файлы
        if (plugin instanceof JavaPlugin) {
            fileManager = new FileManager();
            fileManager.registerFiles();
        }

        /// Инициализация БД
        hikariConnection = HikariConnectionImpl.builder()
                .host(Config.getString("database.host"))
                .user(Config.getString("database.user"))
                .pass(Config.getString("database.pass"))
                .data(Config.getString("database.data"))
                .build();

        /// Создание таблиц БД
        createTables();
    }

    public void createTables() {
        hikariConnection.createTable("Identifiers", "`Id` INT NOT NULL PRIMARY KEY AUTO_INCREMENT, `Name` VARCHAR(16) NOT NULL");
        hikariConnection.createTable("Groups", "`Id` INT NOT NULL PRIMARY KEY, `Group` VARCHAR(255) NOT NULL");

        hikariConnection.createTable("CustomKits",
                "`Id` INT NOT NULL PRIMARY KEY," +
                        "`NoDebuff` JSON DEFAULT NULL," +
                        "`Debuff` JSON DEFAULT NULL," +
                        "`Vanilla` JSON DEFAULT NULL," +
                        "`Axe` JSON DEFAULT NULL," +
                        "`Gapple` JSON DEFAULT NULL"
        );

        hikariConnection.createTable("Statistics",
                "`Id` INT NOT NULL PRIMARY KEY," +
                        "`Name` VARCHAR(16) NOT NULL," +
                        "`NoDebuff` JSON DEFAULT NULL," +
                        "`Debuff` JSON DEFAULT NULL," +
                        "`Vanilla` JSON DEFAULT NULL," +
                        "`Axe` JSON DEFAULT NULL," +
                        "`Gapple` JSON DEFAULT NULL"
        );
    }

    {
        instance = this;
    }
}