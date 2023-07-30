package dev.core.menus;

import dev.core.api.Config;
import dev.core.api.map.ParkourMap;
import dev.core.maps.points.CheckPointMap;
import dev.core.maps.points.PointMap;
import games.negative.framework.gui.GUI;
import games.negative.framework.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class PointsMenu extends GUI {
    private final ParkourMap parkourMap;
    private final Player user;
    private final Config config;
    public PointsMenu(Config config,String title, int rows, ParkourMap parkourMap, Player user) {
        super(title, rows);
        this.config = config;
        this.parkourMap = parkourMap;
        this.user = user;
        addPointsItems();
    }

    private void addPointsItems() {
        List<CheckPointMap> points = parkourMap.getPoints();
        int slot = 0;
        String type = config.getConfig().getString("items.check-item.type");
        for (PointMap point : points) {
            Location pointLocation = point.getLocation();
            if (pointLocation != null) {
                ItemStack pointItem = new ItemStack(Material.valueOf(type)); // Replace with desired point item
                ItemMeta meta = pointItem.getItemMeta();
                String formattedLoc = String.format("X: %.1f, Y: %.1f, Z: %.1f", pointLocation.getX(), pointLocation.getY(), pointLocation.getZ());
                List<String> lore = config.getConfig().getStringList("items.check-item.lore").stream().map(s -> s.replace("%start_point_loc%",formattedLoc)).collect(Collectors.toList());
                String spawnPointName = config.getConfig().getString("items.check-item.displayname").replace("%point_id%",String.valueOf(point.getOrder()));
                meta.setDisplayName(Utils.color(spawnPointName));
                meta.setLore(lore);
                pointItem.setItemMeta(meta);
                // Set the item name and lore to display point location info
                int finalSlot = slot;
                setItemClickEvent(slot, (player) -> pointItem,(player, event) ->  {
                    user.teleport(pointLocation);
                    user.sendMessage("You have been teleported to Point " + (finalSlot) + " Location");
                });

                slot++;
            }
        }
    }
}