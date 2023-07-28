package dev.parkour.holograms.versions;

import dev.parkour.holograms.HoloManager;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class HoloManagerImpl_1_16_5 extends HoloManager<Holo_1_16_5> {
    public HoloManagerImpl_1_16_5(JavaPlugin plugin) {
        super(plugin);
    }
    @Override
    public Holo_1_16_5 newHologramInstance(Location location) {
        return new Holo_1_16_5(location);
    }
}
