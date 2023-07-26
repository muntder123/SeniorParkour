package dev.parkour.core.listeners;

import dev.parkour.Parkour;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.enums.CompletionReason;
import dev.parkour.api.map.sessions.ParkourSession;
import dev.parkour.api.structure.ParkourMapManager;
import dev.parkour.api.users.User;
import dev.parkour.api.users.UserManager;
import dev.parkour.maps.points.PointMap;
import dev.parkour.records.MapRecord;
import dev.parkour.utils.TimeUtil;
import games.negative.framework.util.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class GameEvents implements Listener {

    private final UserManager userManager;
    private final ParkourMapManager manager;
    private HashSet<UUID> coolDownUsers = new HashSet<>();
    public GameEvents(){
        this.userManager = Parkour.getInstance().getUserManager();
        this.manager = Parkour.getInstance().getManager();
    }
    @EventHandler
    public void pressureInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final User parkourPlayer = userManager.getPlayer(player.getUniqueId());

        Block clickedBlock = event.getClickedBlock();
        if (parkourPlayer == null || clickedBlock == null)
            return;

        ItemStack item = event.getItem();
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
                        if (!parkourPlayer.isEligable(map)) {
                            return;
                        }
                        if(player.getGameMode() != GameMode.ADVENTURE) {
                            player.setGameMode(GameMode.ADVENTURE);
                        }
                        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                            if(activePotionEffect.getType() != PotionEffectType.JUMP || activePotionEffect.getType() != PotionEffectType.SPEED)continue;
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
                        player.sendMessage(ChatColor.YELLOW + "You've started a parkour round in "
                                + map.getDisplayName() + ", finish it as soon as possible!");
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 10F, 2F);
                        break;
                    }
                }
            } else {
                final ParkourSession session = parkourPlayer.Session();
                final ParkourMap map = session.getCurrentMap();
                final PointMap checkPoint = map.getPoint(clickedBlock.getLocation());

                if (checkPoint == null) {
                    if (map.getEndLocation() == null)
                        return;
                    final Location plateLocation = clickedBlock.getLocation(),
                            endPointLocation = map.getEndLocation().getLocation();
                    if (plateLocation.getBlockX() == endPointLocation.getBlockX() &&
                            plateLocation.getBlockY() == endPointLocation.getBlockY() &&
                            plateLocation.getBlockZ() == endPointLocation.getBlockZ()) {
                        // This check to ensure that the player completed all checkpoints.
                        if (session.getCurrentCheckpoint() < map.getPoints().size()) {
                            if (!coolDownUsers.contains(player.getUniqueId())) {
                                player.sendMessage(ChatColor.RED + "You must reach all the checkpoints before finishing the map!");
                                coolDownUsers.add(player.getUniqueId());
                                Bukkit.getScheduler().runTaskLater( Parkour.getInstance(),
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

                        player.sendMessage(Utils.color(
                                "&eYou've finished &f" + map.getDisplayName() + "&e map in &f"
                                        + TimeUtil.formatTime(playTime, false)
                                        + (newRecord ? " &6&lNEW RECORD!" : "")
                        ));
                        parkourPlayer.Session().endSession(CompletionReason.NormalCompletionReason);
                        player.chat("/spawn");
                        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10F, 1F);
                    }
                    return;
                }
                // Check if the player passed this checkpoint.
                if (checkPoint.getOrder() <= session.getCurrentCheckpoint())
                    return;
                // Check if the player skipped the previous checkpoint.
                if (checkPoint.getOrder() > session.getCurrentCheckpoint() + 1) {
                    if (!coolDownUsers.contains(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You have skipped checkpoint #"
                                + session.getCurrentCheckpoint() + 1 + ", please go back and pass it!");
                        coolDownUsers.add(player.getUniqueId());
                        Bukkit.getScheduler().runTaskLater(Parkour.getInstance(),
                                () -> coolDownUsers.remove(player.getUniqueId()), 60L);
                    }
                    return;
                }
                if (session.setCurrentCheckpoint(checkPoint)) {
                    player.sendMessage(ChatColor.YELLOW + "You've reached checkpoint #" + checkPoint.getOrder());
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 10F, 2F);
                }
            }
        }
    }
}
