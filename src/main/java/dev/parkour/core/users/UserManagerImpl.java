package dev.parkour.core.users;

import com.zaxxer.hikari.HikariDataSource;
import dev.parkour.Parkour;
import dev.parkour.api.Storage;
import dev.parkour.api.users.User;
import dev.parkour.api.users.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserManagerImpl implements UserManager {

    private final HashMap<UUID,User> map = new HashMap<>();
    private Parkour parkour;

    public UserManagerImpl(Parkour parkour){
        this.parkour = parkour;
    }
    @Override
    public void LoadAllUsers() {
        for(Player player : Bukkit.getOnlinePlayers()){
            loadPlayer(player.getUniqueId()).thenAccept(this::cache);
        }
    }

    @Override
    public CompletableFuture<User> loadPlayer(UUID uuid) {
        Storage<HikariDataSource> storage = parkour.getStorage();
        return storage.fetchRecordNow(uuid);
    }

    @Override
    public User getPlayer(UUID uuid) {
        return map.get(uuid);
    }

    @Override
    public void cache(User player) {
        if (this.map.containsKey(player.uuid())) {
            this.map.replace(player.uuid(), player);
            System.out.println("replcing data " + player.uuid());
        }

        else this.map.put(player.uuid(), player);
        System.out.println("putting data.");
    }

    @Override
    public Collection<User> getLoadedPlayers() {
        return null;
    }
}
