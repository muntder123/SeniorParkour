package dev.parkour.maps.points;

import dev.parkour.api.map.locations.PointType;
import lombok.Getter;
import org.bukkit.Location;
@Getter
public class BuilderPoint {
    private Location location;
    private PointType type;
    private int order = -1; // Set a default value for the order

    public BuilderPoint withLocation(Location location) {
        this.location = location;
        return this;
    }

    public BuilderPoint withType(PointType type) {
        this.type = type;
        return this;
    }

    public BuilderPoint withOrder(int order) {
        this.order = order;
        return this;
    }

    public PointMap build() {
        return new PointMap(location, type, order);
    }
}
