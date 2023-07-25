package dev.parkour.api.map.locations;

public interface PointMap extends CheckPointMap{
    @Override
    default PointType getType() {
        return PointType.CHECKPOINT;
    }
    /**
     * @return Gets the order of the current checkpoint.
     */
    int getOrder();
}
