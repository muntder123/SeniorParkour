package dev.core.maps.points;

import dev.core.api.map.locations.PointType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
@Getter
@Setter
public class PointMap {
    private Location location;
    private PointType type;
    private int order;


    public PointMap(Location location,PointType type,int order){
        this.location = location;
        this.type = type;
        this.order = order;
    }
    public PointMap(PointMap build) {
        this.location = build.getLocation();
        this.type = build.getType();
        this.order = build.getOrder();
    }
}