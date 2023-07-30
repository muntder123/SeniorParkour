package dev.core.holograms;

import dev.api.holograms.Holo;
import dev.api.holograms.HologramManager;
import dev.core.Parkour;
import dev.core.api.Config;
import dev.core.api.Storage;
import dev.core.api.map.ParkourMap;
import dev.core.api.structure.ParkourMapManager;
import dev.core.api.users.User;
import dev.core.api.users.UserManager;
import dev.core.records.MapRecord;
import dev.core.storage.TopPlayerData;
import dev.core.utils.TimeUtil;
import games.negative.framework.util.Utils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class HoloUpdatePlayers extends BukkitRunnable {

    private final HologramManager<?> holoManager;
    private final ParkourMapManager parkourMapManager;
    private final Storage<?> storage;

    public HoloUpdatePlayers(Parkour parkour) {
        this.holoManager = parkour.getHologramManager();
        this.parkourMapManager = parkour.getManager();
        this.storage = Parkour.getInstance().getStorage();
    }


    @SneakyThrows
    @Override
    public void run() {
        for (ParkourMap map : parkourMapManager.getMaps()) {
            if (map == null) continue;

            Config config = Parkour.getInstance().getMessages();
            String mapName = config.getConfig().getString("messages.top.parkourname");
            String yourPos = config.getConfig().getString("messages.top.your-pls");
            List<String> tops = config.getConfig().getStringList("messages.top.topPlayers");

            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                Holo hologram = holoManager.getHologram(map.getId());
                if (hologram == null) continue;

                // Get the top players for the specific parkour map
                Map<UUID, TopPlayerData> topPlayers = storage.getTopPlayers(map);

                // Prepare hologram lines
                List<String> hologramLines = new ArrayList<>();

                List<TopPlayerData> updatedList = topPlayers.values().stream()
                        .sorted(Comparator.comparingInt(TopPlayerData::getPosition))
                        .collect(Collectors.toList());
                // Add the parkour name to the hologram lines

                // Fetch the current user's data from UserManager (assuming UserManager contains a method to get the user by UUID)
                UserManager userManager = Parkour.getInstance().getUserManager();
                User user = userManager.getPlayer(player.getUniqueId());
                if (user == null) continue;
                MapRecord mapRecord = user.getMapRecord(map);
                if (mapRecord == null) continue;
                TopPlayerData topPlayerData1 = topPlayers.get(player.getUniqueId());
                hologramLines.add(Utils.color(mapName.replace("%map_id%", map.getDisplayName())));

                if(topPlayerData1 != null) {
                    int position = !(topPlayerData1.getPosition() <= 0) ? topPlayerData1.getPosition() : 9999;
                    long time = !(topPlayerData1.getTime() <= 0) ? topPlayerData1.getTime() : 1L;
                    hologramLines.add(Utils.color(yourPos.replace("%parkour_pos%", String.valueOf(position)).replace("%time%", TimeUtil.formatTime(time, true))));
                }

                for (TopPlayerData topPlayerData : updatedList) {
                    String playerName = (topPlayerData.getPlayerName() != null) ? topPlayerData.getPlayerName() : "No Top User";
                    int position = !(topPlayerData.getPosition() <= 0) ? topPlayerData.getPosition() : 0;
                    long time = !(topPlayerData.getTime() <= 0) ? topPlayerData.getTime() : 1L;
                        String lineTemplate = tops.get(position -1 );
                        String line = lineTemplate.replace("%user_id_" + position + "%", String.valueOf(position))
                                .replace("%name_" + position + "%", playerName)
                                .replace("%time_" + position + "%", TimeUtil.formatTime(time, false));
                        hologramLines.add(Utils.color(line));
                }

                // Update the hologram with the lines
                hologram.updateLines(hologramLines);
                if(player.getPlayer() == null)continue;
                hologram.show(player.getPlayer());
            }
        }
    }
}
