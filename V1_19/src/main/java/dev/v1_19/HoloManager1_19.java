package dev.v1_19;

import dev.api.holograms.HologramManager;
import org.bukkit.Location;

public class HoloManager1_19 extends HologramManager<Holo_1_19> {
    @Override
    public Holo_1_19 newHologramInstance(Location location) {
        return new Holo_1_19(location);
    }
}