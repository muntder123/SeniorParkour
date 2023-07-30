package dev.core.maps.points;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class PointAdapter implements JsonSerializer<CheckPointMap>, JsonDeserializer<CheckPointMap> {
    @Override
    public JsonElement serialize(CheckPointMap mapPoint, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("order",mapPoint.getOrder());
        jsonObject.addProperty("world", mapPoint.getLocation().getWorld().getName());
        jsonObject.addProperty("x", mapPoint.getLocation().getX());
        jsonObject.addProperty("y", mapPoint.getLocation().getY());
        jsonObject.addProperty("z", mapPoint.getLocation().getZ());
        jsonObject.addProperty("yaw", mapPoint.getLocation().getYaw());
        jsonObject.addProperty("pitch", mapPoint.getLocation().getPitch());

        return jsonObject;
    }

    @Override
    public CheckPointMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int order = jsonObject.get("order").getAsInt();
        String world = jsonObject.get("world").getAsString();
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.get("yaw").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();
        Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        return new CheckPointMap(location,order);
    }
}