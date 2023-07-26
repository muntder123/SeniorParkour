package dev.parkour.menus;

import dev.parkour.api.map.ParkourMap;
import dev.parkour.maps.points.PointMap;
import games.negative.framework.gui.GUI;
import games.negative.framework.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PointsMenu extends GUI {
    private final ParkourMap parkourMap;
    private final Player user;

    public PointsMenu(String title, int rows, ParkourMap parkourMap, Player user) {
        super(title, rows);
        this.parkourMap = parkourMap;
        this.user = user;
        addPointsItems();
    }

    private void addPointsItems() {
        List<PointMap> points = parkourMap.getPoints();
        int slot = 0;
        for (PointMap point : points) {
            Location pointLocation = point.getLocation();
            if (pointLocation != null) {
                ItemStack pointItem = new ItemStack(Material.PAPER); // Replace with desired point item
                ItemMeta meta = pointItem.getItemMeta();
                String pointName = "Point " + (slot);
                meta.setDisplayName(Utils.color("&b"+pointName));
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