package dev.core.holograms;

import dev.api.holograms.Holo;
import dev.api.holograms.HologramManager;
import dev.core.Parkour;
import dev.core.api.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import java.util.*;

public class HoloBoardSetup {

    private final HologramManager<?> hologramManager;
    private final Config config;
    private Map<String,Location> locationMap = new HashMap<>();
    public HoloBoardSetup(Parkour plugin,Config config){
        this.hologramManager = plugin.getHologramManager();
        this.config = config;
        loadHologramLocations();
        spawnLeaderBoards();

    }

    /**
     *   save hologram location in the config file so can be used later when server restart .
     * */
    public void saveHologramLocations(String mapId, Location location) throws IOException {
       locationMap.put(mapId, location);
        saveToConfig();
    }

    public void loadHologramLocations() {
        if(config.getConfig().getConfigurationSection("holograms") == null)return;
        for (String mapId : config.getConfig().getConfigurationSection("holograms").getKeys(false)) {

            if(mapId == null)continue;
            String locationString = config.getConfig().getString("holograms." + mapId);
            if(locationString == null)continue;
            Location location = stringToLocation(locationString);
            if(location == null)continue;
                locationMap.put(mapId,location);
        }
    }

    public void spawnleader(String name,Location location){
        Holo holo = hologramManager.createHologram(name,location);
        holo.showAll();
    }

    public Location getLoc(String name){
        return locationMap.get(name);
    }

    private void spawnLeaderBoards(){
        locationMap.forEach((s, location) -> {
            if(s == null)return;
            if(location == null)return;
            Holo holo = hologramManager.createHologram(s,location);
            holo.showAll();
        });
    }

    private Collection<Location> locations(){
        return Collections.unmodifiableCollection(locationMap.values());
    }
    private void saveToConfig() throws IOException {
        for (String mapId : locationMap.keySet()) {
            Location location = locationMap.get(mapId);
            if (location != null) {
                String locationString = locationToString(location);
                config.getConfig().set("holograms." + mapId, locationString);
            }
        }
        config.getConfig().save(config.getFile());
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }

    // Utility method to convert String to Location
    private Location stringToLocation(String locationString) {
        String[] parts = locationString.split(",");
        if (parts.length == 6) {
            World world = Bukkit.getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }
}


