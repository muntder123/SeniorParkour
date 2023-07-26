package dev.parkour.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.locations.PointType;
import dev.parkour.api.structure.ParkourMapManager;
import dev.parkour.maps.MapImpl;
import dev.parkour.maps.points.EndCheckPoint;
import dev.parkour.maps.points.StartCheckPoint;
import dev.parkour.menus.MapInfoMenu;
import games.negative.framework.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("parkour")
public class ParkourCommands extends BaseCommand {

    private final ParkourMapManager manager;

    public ParkourCommands(ParkourMapManager manager){
        this.manager = manager;
    }


    @Default
    @Subcommand("help")
    @CommandPermission("parkour.command.help")
    public void onHelp(Player player){
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + " /parkour help - Shows this list.");
        player.sendMessage(ChatColor.GRAY + " /parkour maps - Shows all maps.");
        player.sendMessage(ChatColor.GRAY + " /parkour reset - teleport the player to last CheckPoint.");
        player.sendMessage(ChatColor.GRAY + " /parkour setup help - Shows setup help list.");
        player.sendMessage("");
    }

    @Subcommand("Info")
    @CommandPermission("parkour.command.setEnd")
    public void onInfo(Player player ,String map){
        ParkourMap parkourMap = manager.getParkourMap(map);
        if(parkourMap == null){
            player.sendMessage("there is no map with this name");
            return;
        }
        new MapInfoMenu("Parkour info",1,parkourMap).open(player);
    }

    @Subcommand("maps")
    public void onMaps(){

    }

    @Subcommand("setup")
    public final class SetupCommand extends BaseCommand {

        @Default
        @Subcommand("help")
        @CommandPermission("parkour.command.setup.help")
        public void onHelp(Player player) {
            player.sendMessage("");
            player.sendMessage("");
            player.sendMessage(ChatColor.GRAY + " /parkour setup help - show the help list.");
            player.sendMessage(ChatColor.GRAY + " /parkour setup createmap <map> - Creates a new Parkour Map.");
            player.sendMessage(ChatColor.GRAY + " /parkour setup setspawn <map> - Sets spawn location for the map.");
            player.sendMessage(ChatColor.GRAY + " /parkour setup setstart <map> - Sets start point for the map.");
            player.sendMessage(ChatColor.GRAY + " /parkour setup setend <map> - Sets end point for the map.");
            player.sendMessage(ChatColor.GRAY + " /parkour setup clearleaderboard <map> - Clears leaderboard for the map.");
            player.sendMessage(ChatColor.GRAY + " /parkour setup setcheckpoint <map> [order] - Sets a checkpoint for the map.");
            player.sendMessage("");
            }

        @Subcommand("create")
        @CommandPermission("parkour.command.create")
        public void onCreate(Player player,String id){
            ParkourMap parkourMap = new MapImpl(id);
            manager.registerMap(parkourMap);
            player.sendMessage("Map Successfully created" + parkourMap.id());
        }

        @Subcommand("setStart")
        @CommandPermission("parkour.command.setstart")
        public void onSetStart(Player player ,String map){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
                player.sendMessage("there is no map with this name");
                return;
            }
            player.sendMessage("Setting the start location Succesed");
            player.sendMessage("Map name " + parkourMap.id());
            parkourMap.setStartLocation(new StartCheckPoint(parkourMap,player.getLocation(), PointType.START));
            manager.saveMap(parkourMap);
        }
        @Subcommand("setEnd")
        @CommandPermission("parkour.command.setEnd")
        public void onSetEnd(Player player ,String map){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
                player.sendMessage("there is no map with this name");
                return;
            }
            player.sendMessage("Setting the end location Succesed");
            player.sendMessage("Map name " + parkourMap.id());
            parkourMap.setEndLocation(new EndCheckPoint(parkourMap, player.getLocation(),PointType.END));
            manager.saveMap(parkourMap);
        }
        @Subcommand("setDisplayName")
        @CommandPermission("parkour.command.DisplayName")
        public void onSetDisplayName(Player player ,String map,String displayName){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
                player.sendMessage("there is no map with this name");
                return;
            }
            player.sendMessage("Setting the end location Succesed");
            player.sendMessage("Map name " + parkourMap.id());
            parkourMap.setDisplayName(Utils.color(displayName));
            manager.saveMap(parkourMap);
        }
        @Subcommand("setPoint")
        @CommandPermission("parkour.command.setstart")
        public void onSetCheckPoint(Player player ,String map,int order){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
                player.sendMessage("there is no map with this name");
                return;
            }
            if(parkourMap.setCheckpoint(order,player.getLocation())){
                player.sendMessage("Check point added Successfully");
            }else {
                player.sendMessage("something went wrong while adding the point");
            }
        }
    }
}
