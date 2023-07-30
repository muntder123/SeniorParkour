package dev.v1_17;

import dev.api.holograms.HologramManager;
import org.bukkit.Location;

public class HoloManager1_17 extends HologramManager<Holo_1_17> {
    @Override
    public Holo_1_17 newHologramInstance(Location location) {
        return new Holo_1_17(location);
    }
}