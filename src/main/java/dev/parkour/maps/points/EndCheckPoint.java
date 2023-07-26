package dev.parkour.maps.points;

import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.locations.PointType;
import org.bukkit.Location;

public class EndCheckPoint extends PointMap{
    private PointType type;
    private ParkourMap parkourMap;
    public EndCheckPoint(ParkourMap parkourMap,Location location, PointType type) {
        super(new BuilderPoint().withLocation(location).withType(PointType.END).build());
        this.parkourMap = parkourMap;
        this.type = type;
    }
}
