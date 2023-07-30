package dev.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.api.holograms.Holo;
import dev.api.holograms.HologramManager;
import dev.core.Parkour;
import dev.core.api.Config;
import dev.core.api.Storage;
import dev.core.api.map.ParkourMap;
import dev.core.api.map.locations.PointType;
import dev.core.api.structure.ParkourMapManager;
import dev.core.maps.MapImpl;
import dev.core.maps.points.EndCheckPoint;
import dev.core.maps.points.StartCheckPoint;
import dev.core.menus.MapInfoMenu;
import dev.core.menus.StatsMenu;
import dev.core.menus.TopMenu;
import dev.core.storage.TopPlayerData;
import dev.core.utils.TimeUtil;
import games.negative.framework.util.Utils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CommandAlias("parkour")
public class ParkourCommands extends BaseCommand {

    private final ParkourMapManager manager;
    private final HologramManager<?> holoManager;

    private final Config messages;

    public ParkourCommands(ParkourMapManager manager) {
        this.manager = manager;
        this.messages = Parkour.getInstance().getMessages();
        this.holoManager = Parkour.getInstance().getHologramManager();
    }


    @Default
    @Subcommand("help")
    @CommandPermission("parkour.command.help")
    public void onHelp(Player player) {
        for (String helpMessage : messages.getConfig().getStringList("messages.help.parkour-command-help")) {
            player.sendMessage(Utils.color(helpMessage));
        }
    }

    @Subcommand("top")
    public void onTop(Player player,String name,int page){
        ParkourMap parkourMap = manager.getParkourMap(name);
        if(parkourMap == null){
            return;
        }
        new TopMenu(parkourMap,page).open(player);
    }

    @Subcommand("stats")
    public void onStats(Player player) {
        new StatsMenu(player, "Test", 3).open(player);
    }

    @Subcommand("Info")
    @CommandPermission("parkour.command.setEnd")
    @CommandCompletion("parkour_name")
    public void onInfo(Player player, String map) {
        ParkourMap parkourMap = manager.getParkourMap(map);
        if (parkourMap == null) {
            player.sendMessage(Utils.color(messages.getConfig().getString("messages.info.parkour-command-info-map-not-found", "no map found.")));
            return;
        }
        new MapInfoMenu("Parkour info", 1, parkourMap).open(player);
    }


    @Subcommand("create")
    @CommandPermission("parkour.command.create")
    @CommandCompletion("parkour_name")
    public void onCreate(Player player, String id) {
        ParkourMap parkourMap = new MapImpl(id);
        manager.registerMap(parkourMap);
        parkourMap.setDisplayName(Utils.color(id));
        String string = messages.getConfig().getString("messages.setup-create.parkour-command-setup-create-success", "created map");
        player.sendMessage(Utils.color(string).replace("%map%", parkourMap.id()));
    }

    @Subcommand("startTime")
    @CommandPermission("parkour.command.setStartTime")
    @CommandCompletion("parkour_name")
    public void onSetTimeStart(Player player, String map, int startTime) {
        ParkourMap parkourMap = manager.getParkourMap(map);
        if (parkourMap == null) {
            String string = messages.getConfig().getString("messages.setup-startTime.parkour-command-setup-startTime-map-not-found", "no map found");
            player.sendMessage(Utils.color(string));
            return;
        }
        String string = messages.getConfig().getString("messages.setup-startTime.parkour-command-setup-startTime-success", "no map found");
        player.sendMessage(Utils.color(string).replace("%startTime%", String.valueOf(startTime)));
        parkourMap.setStartTime(startTime);
        manager.saveMap(parkourMap);
    }


    @Subcommand("setStart")
    @CommandPermission("parkour.command.setstart")
    @CommandCompletion("parkour_name")
    public void onSetStart(Player player, String map) {
        ParkourMap parkourMap = manager.getParkourMap(map);
        if (parkourMap == null) {
            String string = messages.getConfig().getString("messages.setup-setStart.parkour-command-setup-setStart-map-not-found", "no map found");
            player.sendMessage(Utils.color(string));
            return;
        }
        String string = messages.getConfig().getString("messages.setup-setStart.parkour-command-setup-setStart-success", "set started loc");
        player.sendMessage(Utils.color(string).replace("%map%", parkourMap.id()));
        parkourMap.setStartLocation(new StartCheckPoint(parkourMap, player.getLocation(), PointType.START));
        manager.saveMap(parkourMap);
    }

    @Subcommand("setEnd")
    @CommandPermission("parkour.command.setEnd")
    @CommandCompletion("parkour_name")
    public void onSetEnd(Player player, String map) {
        ParkourMap parkourMap = manager.getParkourMap(map);
        if (parkourMap == null) {
            String message = messages.getConfig().getString("messages.setup-setEnd.parkour-command-setup-setEnd-map-not-found", "&cNo map found with the name %map%.");
            player.sendMessage(Utils.color(message.replace("%map%", map)));
            return;
        }
        String successMessage = messages.getConfig().getString("messages.setup-setEnd.parkour-command-setup-setEnd-success", "&aSet the end location for map %map%.");
        player.sendMessage(Utils.color(successMessage.replace("%map%", parkourMap.id())));
        parkourMap.setEndLocation(new EndCheckPoint(parkourMap, player.getLocation(), PointType.END));
        manager.saveMap(parkourMap);
    }

    @Subcommand("setDisplayName")
    @CommandPermission("parkour.command.DisplayName")
    @CommandCompletion("parkour_name , displayName")
    public void onSetDisplayName(Player player, String map, String displayName) {
        ParkourMap parkourMap = manager.getParkourMap(map);
        if (parkourMap == null) {
            String message = messages.getConfig().getString("messages.setup-setDisplayName.parkour-command-setup-setDisplayName-map-not-found", "&cNo map found with the name %map%.");
            player.sendMessage(Utils.color(message.replace("%map%", map)));
            return;
        }

        String successMessage = messages.getConfig().getString("messages.setup-setDisplayName.parkour-command-setup-setDisplayName-success", "&aSet the display name for map %map%.");
        player.sendMessage(Utils.color(successMessage.replace("%map%", parkourMap.id())));
        parkourMap.setDisplayName(Utils.color(displayName));
        manager.saveMap(parkourMap);
    }

    @Subcommand("setPoint")
    @CommandPermission("parkour.command.setstart")
    @CommandCompletion("parkour_name , int order")
    public void onSetCheckPoint(Player player, String map, int order) {
        ParkourMap parkourMap = manager.getParkourMap(map);
        if (parkourMap == null) {
            String message = messages.getConfig().getString("messages.setup-setPoint.parkour-command-setup-setPoint-map-not-found", "&cNo map found with the name %map%.");
            player.sendMessage(Utils.color(message.replace("%map%", map)));
            return;
        }
        if (parkourMap.setCheckpoint(order, player.getLocation())) {
            String successMessage = messages.getConfig().getString("messages.setup-setPoint.parkour-command-setup-setPoint-success", "&aAdded checkpoint #%order% for map %map%.");
            player.sendMessage(Utils.color(successMessage.replace("%order%", String.valueOf(order)).replace("%map%", parkourMap.id())));
        } else {
            String successMessage = messages.getConfig().getString("messages.setup-setPoint.parkour-command-setup-setPoint-error\", \"&cSomething went wrong while adding the checkpoint.");
            player.sendMessage(Utils.color(successMessage.replace("%order%", String.valueOf(order)).replace("%map%", parkourMap.id())));
        }
    }

    @SneakyThrows
    @Subcommand("settop")
    @CommandCompletion("map_name")
    @CommandPermission("parkour.command.settop")
    public void onSetTop(Player player, String map) {
        ParkourMap parkourMap = manager.getParkourMap(map);
        Config config = Parkour.getInstance().getMessages();
        String mapName = config.getConfig().getString("messages.top.parkourname");
        if (parkourMap == null) return;
        Storage<?> storage = Parkour.getInstance().getStorage();
        Map<UUID, TopPlayerData> topPlayers = storage.getTopPlayers(parkourMap);
        List<String> hologramLines = new ArrayList<>();
        // Add the parkour name to the hologram lines
        String yourPos = config.getConfig().getString("messages.top.your-pls");
        List<String> tops = config.getConfig().getStringList("messages.top.topPlayers");
        TopPlayerData topPlayerData1 = topPlayers.get(player.getUniqueId());
        hologramLines.add(Utils.color(mapName.replace("%map_id%", parkourMap.getDisplayName())));
        if (topPlayerData1 != null) {
            int position = !(topPlayerData1.getPosition() <= 0) ? topPlayerData1.getPosition() : 9999;
            long time = !(topPlayerData1.getTime() <= 0) ? topPlayerData1.getTime() : 1L;
            hologramLines.add(Utils.color(yourPos.replace("%parkour_pos%", String.valueOf(position)).replace("%time%", TimeUtil.formatTime(time, true))));
        }
        for (TopPlayerData topPlayerData : topPlayers.values()) {
            String playerName = (topPlayerData.getPlayerName() != null) ? topPlayerData.getPlayerName() : "No Top User";
            int position = !(topPlayerData.getPosition() <= 0) ? topPlayerData.getPosition() : 9999;
            long time = !(topPlayerData.getTime() <= 0) ? topPlayerData.getTime() : 1L;

            String lineTemplate = tops.get(position - 1);
            String line = lineTemplate.replace("%user_id_" + position + "%", String.valueOf(position))
                    .replace("%name_" + position + "%", playerName)
                    .replace("%time_" + position + "%", TimeUtil.formatTime(time, false));
            hologramLines.add(Utils.color(line));
        }
        Parkour.getInstance().getHoloBoardSetup().saveHologramLocations(parkourMap.id(),player.getLocation());
        Bukkit.getScheduler().runTaskLaterAsynchronously(Parkour.getInstance(),() -> {
            Location loc = Parkour.getInstance().getHoloBoardSetup().getLoc(parkourMap.id());
            Parkour.getInstance().getHoloBoardSetup().spawnleader(parkourMap.id(),loc);
        },2L);
    }
}
