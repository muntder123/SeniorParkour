package dev.parkour.maps.points;

import dev.parkour.api.map.locations.PointType;
import lombok.Getter;
import org.bukkit.Location;
@Getter
public class CheckPointMap extends PointMap {
    private int order;

    public CheckPointMap(Location location, int order) {
        super(new BuilderPoint().withLocation(location).withType(PointType.CHECKPOINT).withOrder(order).build());
        this.order = order;
    }
}