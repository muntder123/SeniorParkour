package dev.parkour.maps.points;

import dev.parkour.Parkour;
import dev.parkour.api.Config;
import dev.parkour.api.map.locations.PointType;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class CheckPointMap extends PointMap {
    private int order;

    public CheckPointMap(Location location, int order) {
        super(new BuilderPoint().withLocation(location).withType(PointType.CHECKPOINT).withOrder(order).build());
        this.order = order;
        ItemStack itemStack = new ItemStack(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        location.getBlock().setType(itemStack.getType());
    }
}