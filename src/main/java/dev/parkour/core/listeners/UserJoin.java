package dev.parkour.core.listeners;

import dev.parkour.Parkour;
import dev.parkour.api.map.enums.CompletionReason;
import dev.parkour.api.structure.ParkourMapManager;
import dev.parkour.api.users.User;
import dev.parkour.api.users.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserJoin implements Listener {

    private final UserManager userManager;

    public UserJoin() {
        this.userManager = Parkour.getInstance().getUserManager();
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        userManager.loadPlayer(player.getUniqueId()).thenAccept(userManager::cache);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User pp = userManager.getPlayer(player.getUniqueId());
        if (pp.Session() != null) {
            userManager.getPlayer(player.getUniqueId()).Session().endSession(CompletionReason.ForcedCompletionReason);
        }
    }
}
