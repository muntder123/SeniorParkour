package dev.core.api.map;

import dev.core.api.structure.Identified;
import dev.core.maps.points.CheckPointMap;
import dev.core.maps.points.PointMap;
import org.bukkit.Location;

import java.util.List;

public interface ParkourMap extends Keyd<String>, Identified {


    String id();

    PointMap getStartLocation();

    int StartTimeLeft();

    void setStartTime(int startTime);

    void setStartLocation(PointMap checkPointMap);

    PointMap getEndLocation();

    boolean setCheckpoint(int order, Location location);
    void setEndLocation(PointMap checkPointMap);

    List<CheckPointMap> getPoints();

    CheckPointMap getPoint( Location location);
    PointMap getPoint(int ordering);


}
