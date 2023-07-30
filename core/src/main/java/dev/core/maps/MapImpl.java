package dev.core.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import dev.core.api.map.ParkourMap;
import dev.core.api.map.locations.PointType;
import dev.core.maps.points.CheckPointMap;
import dev.core.maps.points.EndCheckPoint;
import dev.core.maps.points.PointMap;
import dev.core.maps.points.StartCheckPoint;
import lombok.Data;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
@Data
public class MapImpl implements ParkourMap {
    @Expose
    @SerializedName("ID")
    private String id;
    @Expose
    @SerializedName("DisplayName")
    private String displayName;
    @Expose
    @SerializedName("StartPoint")
    private PointMap startPoint;

    @Expose
    @SerializedName("EndPoint")
    private PointMap endPoint;
    @Expose
    @SerializedName("startTime")
    private int startTime;
    @Expose
    @SerializedName("points")
    private List<CheckPointMap> getPoints;
    public MapImpl(String id){
        this.id = id;
        this.getPoints = new ArrayList<>();
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public PointMap getStartLocation() {
        return startPoint;
    }

    @Override
    public int StartTimeLeft() {
        return startTime;
    }

    @Override
    public void setStartTime(int startTime){
        this.startTime = startTime;
    }
    @Override
    public void setStartLocation(PointMap checkPointMap) {
        this.startPoint = new StartCheckPoint(this,checkPointMap.getLocation(), PointType.START);
    }

    @Override
    public PointMap getEndLocation() {
        return endPoint;
    }

    @Override
    public boolean setCheckpoint(int order, Location location) {
            final Location loc = location.clone();
            CheckPointMap checkPointMap = new CheckPointMap(loc,order);
            checkPointMap.setLocation(location);
            getPoints().add(checkPointMap);
            return true;
        }

    @Override
    public void setEndLocation(PointMap checkPointMap) {
        this.endPoint = new EndCheckPoint(this,checkPointMap.getLocation(),PointType.END);
    }

    @Override
    public List<CheckPointMap> getPoints() {
        return getPoints;
    }

    @Override
    public CheckPointMap getPoint(Location location) {
        return getPoints().stream().filter(checkPoint ->
                        checkPoint.getLocation().getBlockX() == location.getBlockX()
                                && checkPoint.getLocation().getBlockY() == location.getBlockY()
                                && checkPoint.getLocation().getBlockZ() == location.getBlockZ()
                                && checkPoint.getLocation().getWorld() == location.getWorld())
                .findFirst().orElse(null);
    }

    @Override
    public CheckPointMap getPoint(int ordering) {
        return getPoints().get(ordering);
    }


    @Override
    public @NotNull String getKey() {
        return id;
    }

    @Override
    public void setKey(@NotNull String key) {
        this.id = key;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
