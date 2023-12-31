package dev.core.api;

import com.google.gson.*;
import dev.core.maps.points.BuilderPoint;
import dev.core.maps.points.PointMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationTypeAdapter implements JsonSerializer<PointMap>, JsonDeserializer<PointMap> {
@Override
public JsonElement serialize(PointMap mapPoint, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", mapPoint.getLocation().getWorld().getName());
        jsonObject.addProperty("x", mapPoint.getLocation().getX());
        jsonObject.addProperty("y", mapPoint.getLocation().getY());
        jsonObject.addProperty("z", mapPoint.getLocation().getZ());
        jsonObject.addProperty("yaw", mapPoint.getLocation().getYaw());
        jsonObject.addProperty("pitch", mapPoint.getLocation().getPitch());

        return jsonObject;
        }

    @Override
    public PointMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String world = jsonObject.get("world").getAsString();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.get("yaw").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();
        Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        return new BuilderPoint().withLocation(location).build();
    }
}