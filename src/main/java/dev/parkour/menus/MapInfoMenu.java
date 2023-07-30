package dev.parkour.menus;

import dev.parkour.Parkour;
import dev.parkour.api.Config;
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
import java.util.stream.Collectors;

public class MapInfoMenu extends GUI {
    private final Config config;
    private String check_item_menu_title;
    public MapInfoMenu(@NotNull String title, int rows, ParkourMap parkourMap) {
        super(title, rows);
        this.config = Parkour.getInstance().getMenus();
        this.check_item_menu_title = config.getConfig().getString("items.check-item.title");
        addCheckPoints(parkourMap);
        addSpawnPointItem(parkourMap);
        addEndPointItem(parkourMap);
    }


    private void addCheckPoints(ParkourMap parkourMap){
        String type = config.getConfig().getString("items.checkPoints.type");
        ItemStack checkpointItem = new ItemStack(Material.valueOf(type));
        ItemMeta itemMeta = checkpointItem.getItemMeta();
        String displayName = config.getConfig().getString("items.checkPoints.displayname");
        itemMeta.setDisplayName(Utils.color(displayName.replace("%map_id%",parkourMap.getDisplayName())));
        List<String> lore = config.getConfig().getStringList("items.checkPoints.lore").stream().map(s -> s.replace("%map_points%",
                String.valueOf(parkourMap.getPoints().size()))).collect(Collectors.toList());
        itemMeta.setLore(Utils.color(lore));
        checkpointItem.setItemMeta(itemMeta);
        setItemClickEvent(4,player -> checkpointItem,(player, event) -> {
                new PointsMenu(config,Utils.color(check_item_menu_title),6,parkourMap,player).open(player);
            });
    }

    private void addSpawnPointItem(ParkourMap parkourMap) {
        Location spawnPoint = parkourMap.getStartLocation().getLocation();
        String type = config.getConfig().getString("items.start-point.type");

        if (spawnPoint != null) {
            ItemStack spawnPointItem = new ItemStack(Material.valueOf(type)); // Replace with desired spawn point item

            // Set the item name and lore to display spawn point info
            ItemMeta itemMeta = spawnPointItem.getItemMeta();
            assert itemMeta != null;
            String formattedLoc = String.format("X: %.1f, Y: %.1f, Z: %.1f", spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ());

            List<String> lore = config.getConfig().getStringList("items.start-point.lore").stream().map(s -> s.replace("%start_point_loc%",formattedLoc)).collect(Collectors.toList());
            String spawnPointName = config.getConfig().getString("items.start-point.displayname");
            itemMeta.setDisplayName(Utils.color(spawnPointName));

            itemMeta.setLore(Utils.color(lore));
            spawnPointItem.setItemMeta(itemMeta);

            setItemClickEvent(2, (player) -> spawnPointItem,(player, event) -> {
                player.teleport(spawnPoint);
            });
        }
    }

    private void addEndPointItem(ParkourMap parkourMap) {
        Location endPoint = parkourMap.getEndLocation().getLocation();
        String type = config.getConfig().getString("items.end-point.type");
        if (endPoint != null) {
            ItemStack spawnPointItem = new ItemStack(Material.valueOf(type));
            ItemMeta itemMeta = spawnPointItem.getItemMeta();

            String spawnPointName = config.getConfig().getString("items.end-point.displayname");
            itemMeta.setDisplayName(Utils.color(spawnPointName));
            String loc = String.format("X: %.1f, Y: %.1f, Z: %.1f", endPoint.getX(), endPoint.getY(), endPoint.getZ());

            List<String> lore = config.getConfig().getStringList("items.end-point.lore").stream().map(s -> s.replace("%end_point_loc%",loc)).collect(Collectors.toList());
            itemMeta.setLore(Utils.color(lore));
            spawnPointItem.setItemMeta(itemMeta);

            setItemClickEvent(6, (player) -> spawnPointItem, (player, event) -> {
                player.teleport(endPoint);
            });

        }
    }
}
