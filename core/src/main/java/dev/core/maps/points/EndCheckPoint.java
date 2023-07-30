package dev.core.maps.points;

import dev.core.api.map.ParkourMap;
import dev.core.api.map.locations.PointType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EndCheckPoint extends PointMap{
    private PointType type;
    private ParkourMap parkourMap;
    public EndCheckPoint(ParkourMap parkourMap,Location location, PointType type) {
        super(new BuilderPoint().withLocation(location).withType(PointType.END).build());
        this.parkourMap = parkourMap;
        this.type = type;
        ItemStack itemStack = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        location.getBlock().setType(itemStack.getType());
    }
}
