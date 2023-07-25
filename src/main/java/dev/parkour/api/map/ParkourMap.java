package dev.parkour.api.map;

import dev.parkour.api.map.locations.CheckPointMap;
import org.bukkit.Location;

import java.util.List;

public interface ParkourMap extends Keyd<String> {


    String id();


    /**
     *
     * @return The start location of a parkourMap
     */
    CheckPointMap getStartLocation();

    /**
     *
     * set a start Location of a parkourMap
     */

    void setStartLocation(CheckPointMap checkPointMap);

    CheckPointMap getEndLocation();

    void setEndLocation(CheckPointMap checkPointMap);

    List<CheckPointMap> getPoints();

    CheckPointMap getPoint(int pointOrder, Location location);

    int getPlaying();

}
