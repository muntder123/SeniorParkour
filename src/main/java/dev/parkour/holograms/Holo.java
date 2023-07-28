package dev.parkour.holograms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface Holo {


    List<String> getLines();

    Location getLocation();


    void show(Player player);

    void hide(Player player);
    void updateLines(List<String> lines);
    default void showAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            show(player);
        }
    }

    default void destroyAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            hide(player);
        }
    }
}
