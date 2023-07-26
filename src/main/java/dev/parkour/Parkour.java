package dev.parkour;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageType;
import com.zaxxer.hikari.HikariDataSource;
import dev.parkour.api.Config;
import dev.parkour.api.Storage;
import dev.parkour.api.structure.ParkourMapManager;
import dev.parkour.api.users.UserManager;
import dev.parkour.commands.ParkourCommands;
import dev.parkour.core.listeners.GameEvents;
import dev.parkour.core.listeners.UserJoin;
import dev.parkour.maps.ParkourManagerImpl;
import dev.parkour.core.users.UserManagerImpl;
import dev.parkour.placeholders.PlaceHolderHook;
import dev.parkour.storage.MySqlStorageImpl;
import games.negative.framework.BasePlugin;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Parkour extends BasePlugin {
    @Getter
    private static Parkour instance;
    @Getter
    private ParkourMapManager manager;
    @Getter
    private Config settings;
    @Getter
    private Storage<HikariDataSource> storage;
    @Getter
    private UserManager userManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();
        instance = this;
        this.settings = new Config("settings.yml",this);
        this.manager = new ParkourManagerImpl(this);
        this.manager.loadMaps();
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.setFormat(MessageType.HELP, ChatColor.YELLOW);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.RED, ChatColor.RED, ChatColor.RED);
        commandManager.registerCommand(new ParkourCommands(manager));
        this.storage = new MySqlStorageImpl();
        storage.init(settings);
        this.userManager = new UserManagerImpl(this);
        Bukkit.getPluginManager().registerEvents(new UserJoin(),this);
        Bukkit.getPluginManager().registerEvents(new GameEvents(),this);
        new PlaceHolderHook().register();

    }

    @Override
    public void onDisable() {
        manager.saveAllMaps();
    }
}
