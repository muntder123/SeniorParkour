package dev.api.holograms;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class HologramManager<H extends Holo> {
    private final HashMap<String, Holo> hologramMap = new HashMap<>();


    public Holo createHologram(String name, Location location) {
        Holo hologram = newHologramInstance(location);
        if (hologramMap.containsKey(name)) {
            hologramMap.remove(name).destroyAll();
        }
        hologramMap.put(name, hologram);
        return hologram;
    }


    public Holo getHologram(String name) {
        return hologramMap.get(name);
    }

    public Collection<Holo> getHoloList(){
        return Collections.unmodifiableCollection(hologramMap.values());
    }

    public abstract H newHologramInstance(Location location);

}