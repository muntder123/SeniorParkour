package dev.core.core.listeners;

import dev.api.holograms.Holo;
import dev.api.holograms.HologramManager;
import dev.core.Parkour;
import dev.core.api.map.enums.CompletionReason;
import dev.core.api.users.User;
import dev.core.api.users.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class UserJoin implements Listener {

    private final UserManager userManager;
    private final HologramManager<?> hologramManager;
    public UserJoin( ){
        this.userManager = Parkour.getInstance().getUserManager();
        this.hologramManager = Parkour.getInstance().getHologramManager();
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
       final Player player = event.getPlayer();
        userManager.loadPlayer(player.getUniqueId()).thenAccept(userManager::cache).thenRun(() -> {
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
       final Player player = event.getPlayer();
        User pp = userManager.getPlayer(player.getUniqueId());
        if (pp.Session() != null) {
            userManager.getPlayer(player.getUniqueId()).Session().endSession(CompletionReason.ForcedCompletionReason);
        }
    }


    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Parkour.getInstance(), () -> {
            for (final Holo hologram : hologramManager.getHoloList()) {
                if (hologram.getLocation().getWorld() == event.getPlayer().getWorld()) {
                    hologram.show(event.getPlayer());
                }
            }
        }, 1L);
    }
    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Parkour.getInstance(), () -> {
            for (final Holo hologram : hologramManager.getHoloList()) {
                if (hologram.getLocation().getWorld() == event.getPlayer().getWorld()) {
                    hologram.show(event.getPlayer());
                }
            }
        }, 1L);
    }
    @EventHandler
    public void onWorldSwitch(final PlayerChangedWorldEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Parkour.getInstance(), () -> {
            for (final Holo hologram : hologramManager.getHoloList()) {
                if (hologram.getLocation().getWorld() == event.getPlayer().getWorld()) {
                    hologram.show(event.getPlayer());
                }
            }
        }, 1L);
    }
}
