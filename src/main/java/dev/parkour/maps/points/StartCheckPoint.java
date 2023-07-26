package dev.parkour.maps.points;

import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.locations.PointType;
import org.bukkit.Location;

public class StartCheckPoint extends PointMap{
    private PointType type;
    private final ParkourMap parkourMap;

    public StartCheckPoint(ParkourMap parkourMap,Location location, PointType type) {
        super(new BuilderPoint().withLocation(location).withType(PointType.START).build());
        this.parkourMap = parkourMap;
        this.type = type;
    }
}
