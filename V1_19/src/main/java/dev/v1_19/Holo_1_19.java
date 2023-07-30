package dev.v1_19;

import dev.api.holograms.Holo;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftItemDisplay;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Holo_1_19 implements Holo {

    private final Location location;
    private final List<ArmorStand> armorStands = new ArrayList<>();

    public Holo_1_19(Location location) {
        this.location = location;
    }

    public Holo_1_19(Location location, String... lines) {
        this(location);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public List<String> getLines() {
        return armorStands.stream().map(EntityLiving -> EntityLiving.getBukkitEntity().getCustomName()).collect(Collectors.toList());
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
            for (ArmorStand armorStand : armorStands) {
                if (lines.get(i).isEmpty()) {
                    armorStand.getBukkitEntity().setCustomNameVisible(true);
                } else {
                    armorStand.getBukkitEntity().setCustomName(ChatColor.translateAlternateColorCodes('&', lines.get(i)));
                }
                lastLocation = new Location(
                        location.getWorld(),
                        armorStand.getBukkitEntity().getLocation().getX(),
                        armorStand.getBukkitEntity().getLocation().getY(),
                        armorStand.getBukkitEntity().getLocation().getZ()
                );
                i++;
            }
            for (int j = i; j < lines.size(); j++) {
                String line = lines.get(j);
                Location updatedLastLocation = lastLocation.clone().add(0, -.3, 0);
                ArmorStand armorStand = new ArmorStand(
                        ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle(),
                        updatedLastLocation.getX(),
                        updatedLastLocation.getY(),
                        updatedLastLocation.getZ()
                );
                armorStand.setNoGravity(true);
                armorStand.setInvisible(true);
                armorStand.setInvulnerable(true);
                armorStand.setCustomNameVisible(true);
                armorStand.getBukkitEntity().setCustomName(ChatColor.translateAlternateColorCodes('&',line));
                armorStands.add(armorStand);
                lastLocation = updatedLastLocation;
            }
        } else {
            int size = armorStands.size();
            for (String line : lines) {
                ArmorStand armorStand = armorStands.get(i);
                armorStand.getBukkitEntity().setCustomNameVisible(!line.isEmpty());
                armorStand.getBukkitEntity().setCustomName(ChatColor.translateAlternateColorCodes('&',line));
                i++;
            }
            for (int j = i; j < size; j++) {
                ArmorStand armorStand = armorStands.remove(j);
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

    private void sendDestroyPacket(Player player, ArmorStand entity) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ClientboundRemoveEntitiesPacket clientboundRemoveEntitiesPacket = new ClientboundRemoveEntitiesPacket(entity.getId());
        craftPlayer.getHandle().connection.send(clientboundRemoveEntitiesPacket);
    }

    private void sendSummonPacket(Player player, ArmorStand armorStand) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ClientboundAddEntityPacket sp = new ClientboundAddEntityPacket(armorStand);
        ClientboundSetEntityDataPacket metadata = new ClientboundSetEntityDataPacket(armorStand.getId(), armorStand.getEntityData().getNonDefaultValues());
        craftPlayer.getHandle().connection.send(sp);
        craftPlayer.getHandle().connection.send(metadata);
    }

}
