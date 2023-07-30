package dev.core;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageType;
import com.zaxxer.hikari.HikariDataSource;
import dev.api.holograms.HologramManager;
import dev.core.api.Config;
import dev.core.api.Storage;
import dev.core.api.structure.ParkourMapManager;
import dev.core.api.users.UserManager;
import dev.core.commands.ParkourCommands;
import dev.core.core.listeners.GameEvents;
import dev.core.core.listeners.UserJoin;
import dev.core.core.manager.ParkourManagerImpl;
import dev.core.core.users.UserManagerImpl;
import dev.core.holograms.HoloBoardSetup;
import dev.core.holograms.HoloUpdatePlayers;
import dev.core.placeholders.PlaceHolderHook;
import dev.core.scoreboard.GameScoreboard;
import dev.core.storage.MySqlStorageImpl;
import dev.v1_16.HoloManager1_16;
import dev.v1_17.HoloManager1_17;
import dev.v1_18.HoloManager1_18;
import dev.v1_19.HoloManager1_19;
import games.negative.framework.BasePlugin;
import games.negative.framework.util.version.ServerVersion;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Parkour extends BasePlugin {
    @Getter
    private static Parkour instance;
    @Getter
    private ParkourMapManager manager;
    @Getter
    private Config settings,scoreboard,messages,menus,holograms;
    @Getter
    private Storage<HikariDataSource> storage;
    @Getter
    private UserManager userManager;
    @Getter
    private HologramManager<?> hologramManager;
    @Getter
    private HoloBoardSetup holoBoardSetup;
    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();
        instance = this;
        this.settings = new Config("settings.yml",this);
        this.scoreboard = new Config("scoreboard.yml",this);
        this.messages = new Config("messages.yml",this);
        this.menus = new Config("menus.yml",this);
        this.holograms = new Config("holograms.yml",this);
        setupVer();
        this.manager = new ParkourManagerImpl(this);
        this.holoBoardSetup = new HoloBoardSetup(this,holograms);
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

    private void setupVer(){
        ServerVersion serverVersion = ServerVersion.fromServerPackageName(Bukkit.getServer().getClass().getPackage().getName());
        if (serverVersion.isAtLeast(ServerVersion.V1_19)) {
            this.hologramManager = new HoloManager1_19();
        } else if (serverVersion.isAtLeast(ServerVersion.V1_18)) {
            this.hologramManager = new HoloManager1_18();
        } else if (serverVersion.isAtLeast(ServerVersion.V1_17)) {
            this.hologramManager = new HoloManager1_17();
        } else if (serverVersion.isAtLeast(ServerVersion.V1_16)) {
            this.hologramManager = new HoloManager1_16();
        }
    }
    @Override
    public void onDisable() {
        manager.saveAllMaps();
    }
}
