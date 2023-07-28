package dev.parkour.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.parkour.Parkour;
import dev.parkour.api.Config;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.locations.PointType;
import dev.parkour.api.structure.ParkourMapManager;
import dev.parkour.maps.MapImpl;
import dev.parkour.maps.points.EndCheckPoint;
import dev.parkour.maps.points.StartCheckPoint;
import dev.parkour.menus.MapInfoMenu;
import games.negative.framework.util.Utils;
import org.bukkit.entity.Player;

@CommandAlias("parkour")
public class ParkourCommands extends BaseCommand {

    private final ParkourMapManager manager;
    private final Config messages;
    public ParkourCommands(ParkourMapManager manager){
        this.manager = manager;
        this.messages = Parkour.getInstance().getMessages();
    }


    @Default
    @Subcommand("help")
    @CommandPermission("parkour.command.help")
    public void onHelp(Player player){
        for (String helpMessage : messages.getConfig().getStringList("messages.help.parkour-command-help")) {
            player.sendMessage(Utils.color(helpMessage));
        }
    }

    @Subcommand("Info")
    @CommandPermission("parkour.command.setEnd")
    @CommandCompletion("parkour_name")
    public void onInfo(Player player ,String map){
        ParkourMap parkourMap = manager.getParkourMap(map);
        if(parkourMap == null){
            player.sendMessage(Utils.color(messages.getConfig().getString("messages.info.parkour-command-info-map-not-found","no map found.")));
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
            for (String helpMessage : messages.getConfig().getStringList("messages.setup-help.parkour-command-setup-help")) {
                player.sendMessage(Utils.color(helpMessage));
            }
        }

        @Subcommand("create")
        @CommandPermission("parkour.command.create")
        @CommandCompletion("parkour_name")
        public void onCreate(Player player,String id){
            ParkourMap parkourMap = new MapImpl(id);
            manager.registerMap(parkourMap);
            parkourMap.setDisplayName(Utils.color(id));
            String string = messages.getConfig().getString("messages.setup-create.parkour-command-setup-create-success","created map");
            player.sendMessage(Utils.color(string).replace("%map%",parkourMap.id()));
        }

        @Subcommand("startTime")
        @CommandPermission("parkour.command.setStartTime")
        @CommandCompletion("parkour_name")
        public void onSetTimeStart(Player player ,String map,int startTime){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
                String string = messages.getConfig().getString("messages.setup-startTime.parkour-command-setup-startTime-map-not-found","no map found");
                player.sendMessage(Utils.color(string));
                return;
            }
            String string = messages.getConfig().getString("messages.setup-startTime.parkour-command-setup-startTime-success","no map found");
            player.sendMessage(Utils.color(string).replace("%startTime%",String.valueOf(startTime)));
            parkourMap.setStartTime(startTime);
            manager.saveMap(parkourMap);
        }


        @Subcommand("setStart")
        @CommandPermission("parkour.command.setstart")
        @CommandCompletion("parkour_name")
        public void onSetStart(Player player ,String map){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
                String string = messages.getConfig().getString("messages.setup-setStart.parkour-command-setup-setStart-map-not-found","no map found");
                player.sendMessage(Utils.color(string));
                return;
            }
            String string = messages.getConfig().getString("messages.setup-setStart.parkour-command-setup-setStart-success","set started loc");
            player.sendMessage(Utils.color(string).replace("%map%",parkourMap.id()));
            parkourMap.setStartLocation(new StartCheckPoint(parkourMap,player.getLocation(), PointType.START));
            manager.saveMap(parkourMap);
        }
        @Subcommand("setEnd")
        @CommandPermission("parkour.command.setEnd")
        @CommandCompletion("parkour_name")
        public void onSetEnd(Player player ,String map){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
                String message = messages.getConfig().getString("messages.setup-setEnd.parkour-command-setup-setEnd-map-not-found", "&cNo map found with the name %map%.");
                player.sendMessage(Utils.color(message.replace("%map%", map)));
                return;
            }
            String successMessage = messages.getConfig().getString("messages.setup-setEnd.parkour-command-setup-setEnd-success", "&aSet the end location for map %map%.");
            player.sendMessage(Utils.color(successMessage.replace("%map%", parkourMap.id())));
            parkourMap.setEndLocation(new EndCheckPoint(parkourMap, player.getLocation(),PointType.END));
            manager.saveMap(parkourMap);
        }

        @Subcommand("setDisplayName")
        @CommandPermission("parkour.command.DisplayName")
        @CommandCompletion("parkour_name , displayName")
        public void onSetDisplayName(Player player ,String map,String displayName){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
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
        public void onSetCheckPoint(Player player ,String map,int order){
            ParkourMap parkourMap = manager.getParkourMap(map);
            if(parkourMap == null){
                String message = messages.getConfig().getString("messages.setup-setPoint.parkour-command-setup-setPoint-map-not-found", "&cNo map found with the name %map%.");
                player.sendMessage(Utils.color(message.replace("%map%", map)));
                return;
            }
            order++;
            if(parkourMap.setCheckpoint(order,player.getLocation())){
                String successMessage = messages.getConfig().getString("messages.setup-setPoint.parkour-command-setup-setPoint-success", "&aAdded checkpoint #%order% for map %map%.");
                player.sendMessage(Utils.color(successMessage.replace("%order%", String.valueOf(order)).replace("%map%", parkourMap.id())));
            }else {
                String successMessage = messages.getConfig().getString("messages.setup-setPoint.parkour-command-setup-setPoint-error\", \"&cSomething went wrong while adding the checkpoint.");
                player.sendMessage(Utils.color(successMessage.replace("%order%", String.valueOf(order)).replace("%map%", parkourMap.id())));
            }
        }
    }
}
