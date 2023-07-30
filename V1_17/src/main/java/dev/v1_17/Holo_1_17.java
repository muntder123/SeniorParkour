package dev.v1_17;

import dev.api.holograms.Holo;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Holo_1_17 implements Holo {

    private final Location location;
    private final List<EntityArmorStand> armorStands = new ArrayList<>();

    public Holo_1_17(Location location) {
        this.location = location;
    }

    public Holo_1_17(Location location, String... lines) {
        this(location);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public List<String> getLines() {
        return armorStands.stream().map(EntityArmorStand::
                getCustomName).filter(Objects::nonNull).map(IChatBaseComponent::getString).collect(Collectors.toList());
    }

    @Override
    public void updateLines(List<String> lines) {
        if (lines.isEmpty()) {
            destroyAll();
            return;
        }
        int i = 0;
        if (lines.size() >= armorStands.size()) {
            Location lastLocation = location.clone();
            for (EntityArmorStand armorStand : armorStands) {
                if (lines.get(i).isEmpty()) {
                    armorStand.setCustomNameVisible(true);
                } else {
                    armorStand.setCustomName(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes(
                            '&',lines.get(i)) + "\"}"));
                }
                lastLocation = new Location(
                        location.getWorld(),
                        armorStand.locX(),
                        armorStand.locY(),
                        armorStand.locZ()
                );
                i++;
            }
            for (int j = i; j < lines.size(); j++) {
                String line = lines.get(j);

                Location updatedLastLocation = lastLocation.clone().add(0, -.3, 0);
                EntityArmorStand armorStand = new EntityArmorStand(
                        ((CraftWorld) location.getWorld()).getHandle(),
                        updatedLastLocation.getX(),
                        updatedLastLocation.getY(),
                        updatedLastLocation.getZ()
                );
                armorStand.setNoGravity(false);
                armorStand.setSmall(true);
                armorStand.setInvisible(true);
                armorStand.setCustomNameVisible(true);
                armorStand.setCustomName(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes(
                        '&',line) + "\"}"));
                armorStands.add(armorStand);
                lastLocation = updatedLastLocation;
            }
        } else {
            int size = armorStands.size();
            for (String line : lines) {
                EntityArmorStand armorStand = armorStands.get(i);
                armorStand.setCustomNameVisible(!line.isEmpty());
                armorStand.setCustomName(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatColor.translateAlternateColorCodes(
                        '&',line) + "\"}"));
                i++;
            }
            for (int j = i; j < size; j++) {
                EntityArmorStand armorStand = armorStands.remove(j);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendDestroyPacket(player, armorStand);
                }
            }
        }
    }

    @Override
    public void hide(Player player) {
        armorStands.forEach(armorStand -> sendDestroyPacket(player, armorStand));
    }

    @Override
    public void show(Player player) {
        armorStands.forEach(armorStand -> sendSummonPacket(player, armorStand));
    }

    private void sendDestroyPacket(Player player, EntityArmorStand entity) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entity.getId());
        craftPlayer.getHandle().b.sendPacket(destroyPacket);
    }

    private void sendSummonPacket(Player player, EntityArmorStand entityArmorStand) {
        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
        ((CraftPlayer) player).getHandle().b.sendPacket(spawnPacket);
        PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), true);
        ((CraftPlayer) player).getHandle().b.sendPacket(metadataPacket);
    }



}
