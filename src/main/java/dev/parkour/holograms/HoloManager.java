package dev.parkour.holograms;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public abstract class HoloManager<H extends Holo> {
    private final HashMap<String, Holo> hologramMap = new HashMap<>();


    public HoloManager(final JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(final PlayerJoinEvent event) {
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    for (final Holo hologram : hologramMap.values()) {
                        if (hologram.getLocation().getWorld() == event.getPlayer().getWorld()) {
                            hologram.show(event.getPlayer());
                            System.out.println("showing the hologram hehe");
                        }
                    }
                }, 1L);
            }
            @EventHandler
            public void onTeleport(final PlayerTeleportEvent event) {
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    for (final Holo hologram : hologramMap.values()) {
                        if (hologram.getLocation().getWorld() == event.getPlayer().getWorld()) {
                            hologram.show(event.getPlayer());
                        }
                    }
                }, 1L);
            }
            @EventHandler
            public void onWorldSwitch(final PlayerChangedWorldEvent event) {
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    for (final Holo hologram : hologramMap.values()) {
                        if (hologram.getLocation().getWorld() == event.getPlayer().getWorld()) {
                            hologram.show(event.getPlayer());
                        }
                    }
                }, 1L);
            }
        }, plugin);
    }

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

    public abstract H newHologramInstance(Location location);

}
