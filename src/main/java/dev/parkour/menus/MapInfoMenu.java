package dev.parkour.menus;

import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.users.User;
import dev.parkour.maps.points.PointMap;
import games.negative.framework.gui.GUI;
import games.negative.framework.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class MapInfoMenu extends GUI {
    public MapInfoMenu(@NotNull String title, int rows, ParkourMap parkourMap) {
        super(title, rows);
        addCheckPoints(parkourMap);
        addSpawnPointItem(parkourMap);
        addEndPointItem(parkourMap);
    }


    private void addCheckPoints(ParkourMap parkourMap){
            ItemStack checkpointItem = new ItemStack(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
            setItemClickEvent(4,player -> checkpointItem,(player, event) -> {
                new PointsMenu("PointsMenu",6,parkourMap,player).open(player);
            });
    }

    private void addSpawnPointItem(ParkourMap parkourMap) {
        Location spawnPoint = parkourMap.getStartLocation().getLocation();
        if (spawnPoint != null) {
            ItemStack spawnPointItem = new ItemStack(Material.BEDROCK); // Replace with desired spawn point item

            // Set the item name and lore to display spawn point info
            ItemMeta itemMeta = spawnPointItem.getItemMeta();
            assert itemMeta != null;
            List<String> lore = new ArrayList<>();
            String spawnPointName = "Start Point";
            itemMeta.setDisplayName(spawnPointName);
            String spawnPointLore = "Coordinates: " + spawnPoint.getX() + ", " +
                    spawnPoint.getY() + ", " + spawnPoint.getZ();
            lore.add(spawnPointLore);
            itemMeta.setLore(lore);
            spawnPointItem.setItemMeta(itemMeta);

            setItemClickEvent(2, (player) -> spawnPointItem,(player, event) -> {
                player.teleport(spawnPoint);
                player.sendMessage("You have been teleported to the Spawn Point");
            });


        }
    }

    private void addEndPointItem(ParkourMap parkourMap) {
        Location endPoint = parkourMap.getEndLocation().getLocation();
        if (endPoint != null) {
            ItemStack spawnPointItem = new ItemStack(Material.BEDROCK); // Replace with desired spawn point item

            // Set the item name and lore to display spawn point info
            ItemMeta itemMeta = spawnPointItem.getItemMeta();
            assert itemMeta != null;
            List<String> lore = new ArrayList<>();
            String spawnPointName = "End Point";
            itemMeta.setDisplayName(spawnPointName);
            String spawnPointLore = "Coordinates: " + endPoint.getX() + ", " +
                    endPoint.getY() + ", " + endPoint.getZ();
            lore.add(Utils.color(spawnPointLore));
            itemMeta.setLore(lore);
            spawnPointItem.setItemMeta(itemMeta);

            setItemClickEvent(6, (player) -> spawnPointItem, (player, event) -> {
                player.teleport(endPoint);
                player.sendMessage("You have been teleported to the Spawn Point");
            });

        }
    }
}
