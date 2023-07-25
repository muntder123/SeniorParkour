package dev.parkour;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Parkour extends JavaPlugin {
    @Getter
    private static Parkour instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
