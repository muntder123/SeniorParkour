package dev.v1_16;

import dev.api.holograms.HologramManager;
import org.bukkit.Location;

public class HoloManager1_16 extends HologramManager<Holo_1_16_5> {
    @Override
    public Holo_1_16_5 newHologramInstance(Location location) {
        return new Holo_1_16_5(location);
    }
}