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
import dev.parkour.core.manager.ParkourManagerImpl;
import dev.parkour.core.users.UserManagerImpl;
import dev.parkour.holograms.HoloManager;
import dev.parkour.holograms.leaderboard.HoloUpdatePlayers;
import dev.parkour.holograms.versions.HoloManagerImpl_1_16_5;
import dev.parkour.placeholders.PlaceHolderHook;
import dev.parkour.scoreboard.GameScoreboard;
import dev.parkour.storage.MySqlStorageImpl;
import games.negative.framework.BasePlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Parkour extends BasePlugin {
    @Getter
    private static Parkour instance;
    @Getter
    private ParkourMapManager manager;
    @Getter
    private Config settings,scoreboard,messages,menus;
    @Getter
    private Storage<HikariDataSource> storage;
    @Getter
    private UserManager userManager;
    @Getter
    private HoloManager<?> hologramManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();
        instance = this;
        this.hologramManager = new HoloManagerImpl_1_16_5(this);

        this.settings = new Config("settings.yml",this);
        this.scoreboard = new Config("scoreboard.yml",this);
        this.messages = new Config("messages.yml",this);
        this.menus = new Config("menus.yml",this);

        this.manager = new ParkourManagerImpl(this);
        this.manager.loadMaps(settings);
        this.manager.setupHolo(settings);

        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.setFormat(MessageType.HELP, ChatColor.YELLOW);
        commandManager.setFormat(MessageType.SYNTAX, ChatColor.RED, ChatColor.RED, ChatColor.RED);
        commandManager.registerCommand(new ParkourCommands(manager));

        this.storage = new MySqlStorageImpl();
        storage.init(settings);

        this.userManager = new UserManagerImpl(this);

        Bukkit.getPluginManager().registerEvents(new UserJoin(),this);
        Bukkit.getPluginManager().registerEvents(new GameEvents(),this);
        Bukkit.getPluginManager().registerEvents(new GameScoreboard(scoreboard),this);
        new PlaceHolderHook().register();
        new HoloUpdatePlayers(this).runTaskTimer(this,20L,20L);

    }

    @Override
    public void onDisable() {
        manager.saveAllMaps();
    }
}
