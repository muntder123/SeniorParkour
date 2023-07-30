package dev.v1_18;

import dev.api.holograms.HologramManager;
import org.bukkit.Location;

public class HoloManager1_18 extends HologramManager<Holo_1_18> {
    @Override
    public Holo_1_18 newHologramInstance(Location location) {
        return new Holo_1_18(location);
    }
}