package dev.parkour.api.map;

import dev.parkour.api.structure.Identified;
import dev.parkour.maps.points.PointMap;
import org.bukkit.Location;

import java.util.List;

public interface ParkourMap extends Keyd<String>, Identified {


    String id();

    PointMap getStartLocation();


    void setStartLocation(PointMap checkPointMap);

    PointMap getEndLocation();

    boolean setCheckpoint(int order, Location location);
    void setEndLocation(PointMap checkPointMap);

    List<PointMap> getPoints();

    PointMap getPoint( Location location);
    PointMap getPoint(int ordering);


}
