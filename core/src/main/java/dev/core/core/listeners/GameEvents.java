package dev.core.core.listeners;

import dev.core.api.Config;
import dev.core.api.map.ParkourMap;
import dev.core.api.map.enums.CompletionReason;
import dev.core.api.structure.ParkourMapManager;
import dev.core.api.users.User;
import dev.core.api.users.UserManager;
import dev.core.Parkour;
import dev.core.core.manager.ParkourSession;
import dev.core.maps.points.CheckPointMap;
import dev.core.records.MapRecord;
import dev.core.utils.TimeUtil;
import games.negative.framework.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GameEvents implements Listener {

    private final UserManager userManager;
    private final ParkourMapManager manager;
    private final Config messages;
    private HashSet<UUID> coolDownUsers = new HashSet<>();
    public GameEvents(){
        this.userManager = Parkour.getInstance().getUserManager();
        this.manager = Parkour.getInstance().getManager();
        this.messages = Parkour.getInstance().getMessages();
    }
    @EventHandler
    public void pressureInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final User parkourPlayer = userManager.getPlayer(player.getUniqueId());

        Block clickedBlock = event.getClickedBlock();
        if (parkourPlayer == null || clickedBlock == null)
            return;
        if (event.getAction() == Action.PHYSICAL && clickedBlock.getType().name().contains("PLATE")) {
            event.setCancelled(true);
            if (parkourPlayer.Session() == null) {
                for (ParkourMap map : manager.getMaps()) {
                    if (map.getStartLocation() == null) continue;
                    final Location plateLocation = clickedBlock.getLocation(),
                            startPointLocation = map.getStartLocation().getLocation();
                    if (plateLocation.getBlockX() == startPointLocation.getBlockX() &&
                            plateLocation.getBlockY() == startPointLocation.getBlockY() &&
                            plateLocation.getBlockZ() == startPointLocation.getBlockZ()) {
                        if (player.getGameMode() != GameMode.ADVENTURE) {
                            player.setGameMode(GameMode.ADVENTURE);
                        }

                        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                            if (activePotionEffect.getType() != PotionEffectType.JUMP || activePotionEffect.getType() != PotionEffectType.SPEED)
                                continue;
                            player.removePotionEffect(activePotionEffect.getType());
                        }
                        CompletableFuture.supplyAsync(() -> {
                            MapRecord record = parkourPlayer.getMapRecord(map);
                            if (record == null) {
                                record = parkourPlayer.setLowestRecord(map, -1).join();
                            }
                            return record;
                        }).thenAcceptAsync(record -> record.setGamesPlayed(record.getGamesPlayed() + 1));
                        parkourPlayer.setCurrentSession(map);
                        String message = messages.getConfig().getString("messages.game-mechanics-started-round",
                                "&eYou've started a parkour round in %map%, finish it as soon as possible!");
                        message = message.replace("%map%", map.getDisplayName());
                        player.sendMessage(Utils.color(message));
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 10F, 2F);
                        break;
                    }
                    Bukkit.getScheduler().runTaskTimer(Parkour.getInstance(),() -> {
                        if(player.isFlying()){
                            if(parkourPlayer.Session() == null)return;
                            parkourPlayer.Session().endSession(CompletionReason.ForcedCompletionReason);

                        }
                    },20L,0);
                }
            } else {
                final ParkourSession session = parkourPlayer.Session();
                final ParkourMap map = session.getCurrentMap();
                final CheckPointMap checkPoint = map.getPoint(clickedBlock.getLocation());
                if (checkPoint == null) {
                    if (map.getEndLocation() == null) {
                        return;
                    }
                    final Location plateLocation = clickedBlock.getLocation(),
                            endPointLocation = map.getEndLocation().getLocation();
                    if (plateLocation.getBlockX() == endPointLocation.getBlockX() &&
                            plateLocation.getBlockY() == endPointLocation.getBlockY() &&
                            plateLocation.getBlockZ() == endPointLocation.getBlockZ()) {
                        // This check to ensure that the player completed all checkpoints.
                        if (session.getCurrentCheckpoint() < map.getPoints().size()) {
                            if (!coolDownUsers.contains(player.getUniqueId())) {
                                String allReached = messages.getConfig().getString("messages.game-mechanics-must-reach-checkpoints");
                                if(allReached != null)
                                player.sendMessage(Utils.color(allReached));
                                coolDownUsers.add(player.getUniqueId());
                                Bukkit.getScheduler().runTaskLater(Parkour.getInstance(),
                                        () -> coolDownUsers.remove(player.getUniqueId()), 60L);
                            }
                            return;
                        }

                        long playTime = System.currentTimeMillis() - session.getStartTime();


                        boolean newRecord = parkourPlayer.hasFinished(map)
                                && parkourPlayer.getMapRecord(map).getLowestRecord() != -1
                                && playTime < parkourPlayer.getMapRecord(map).getLowestRecord();
                        if (!parkourPlayer.hasFinished(map)) {
                            parkourPlayer.setLowestRecord(map, playTime);
                        } else {
                            if (newRecord) {
                                parkourPlayer.setLowestRecord(map, playTime);
                            }
                        }
                        final MapRecord record = parkourPlayer.getMapRecord(map);
                        record.setCompletions(record.getCompletions() + 1);
                        String finishMessage = messages.getConfig().getString("messages.game-mechanics-finish-map",
                                "&eYou've finished &f%map% &e map in &f%time% %record%");
                        finishMessage = finishMessage.replace("%map%", map.getDisplayName())
                                .replace("%time%", TimeUtil.formatTime(playTime, false))
                                .replace("%record%", newRecord ? "&6&lNEW RECORD!" : "");

                        player.sendMessage(Utils.color(finishMessage));

                        parkourPlayer.Session().endSession(CompletionReason.NormalCompletionReason);
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10F, 1F);
                    }
                    return;
                }



                if (checkPoint.getOrder() <= session.getCurrentCheckpoint())
                    return;

                    if (checkPoint.getOrder() > session.getCurrentCheckpoint() + 1) {
                        if (!coolDownUsers.contains(player.getUniqueId())) {
                            String skipMessage = messages.getConfig().getString("messages.game-mechanics-skip-checkpoint",
                                    "&cYou have skipped checkpoint #%checkpoint%, please go back and pass it!");
                            skipMessage = skipMessage.replace("%checkpoint%", String.valueOf(session.getCurrentCheckpoint() + 1));
                            player.sendMessage(Utils.color(skipMessage));
                            coolDownUsers.add(player.getUniqueId());
                            Bukkit.getScheduler().runTaskLater(Parkour.getInstance(),
                                    () -> coolDownUsers.remove(player.getUniqueId()), 60L);
                        }
                        return;
                    }
                    if (session.setCurrentCheckpoint(checkPoint)) {
                        long currentTimeMillis = System.currentTimeMillis();
                        int remainingTimeInSeconds = map.StartTimeLeft();
                        long elapsedTimeInSeconds = (currentTimeMillis - session.getStartTime()) / 1000;
                        if (elapsedTimeInSeconds > remainingTimeInSeconds) {
                            parkourPlayer.Session().endSession(CompletionReason.ForcedCompletionReason);
                            String outOfTimeMessage = messages.getConfig().getString("messages.game-mechanics-out-of-time",
                                    "&eYou are out of time, sorry!");
                            player.sendMessage(Utils.color(outOfTimeMessage));
                            return;
                        }

                        String checkpointReachedMessage = messages.getConfig().getString("messages.game-mechanics-checkpoint-reached",
                                "&eYou've reached checkpoint #%checkpoint%#");
                        checkpointReachedMessage = checkpointReachedMessage.replace("%checkpoint%", String.valueOf(checkPoint.getOrder()));
                        player.sendMessage(Utils.color(checkpointReachedMessage));
                        event.setCancelled(true);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 10F, 2F);
                }
            }
        }
    }
}
