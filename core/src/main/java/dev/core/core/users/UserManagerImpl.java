package dev.core.core.users;

import com.zaxxer.hikari.HikariDataSource;
import dev.core.Parkour;
import dev.core.api.Storage;
import dev.core.api.users.User;
import dev.core.api.users.UserManager;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UserManagerImpl implements UserManager {
    /**
     * UserManager responsable for User load , cache and load all Users!
     */
    private final HashMap<UUID,User> map = new HashMap<>();
    private Parkour parkour;

    private List<User> loadedUsers = new ArrayList<>();
    public UserManagerImpl(Parkour parkour){
        this.parkour = parkour;
    }
    @SneakyThrows
    @Override
    public void LoadAllUsers() {
        for(Player player : Bukkit.getOnlinePlayers()){
            CompletableFuture<User> userCompletableFuture = loadPlayer(player.getUniqueId());
            userCompletableFuture.thenAccept(this::cache);
            loadedUsers.add(userCompletableFuture.get());
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
        }

        else this.map.put(player.uuid(), player);
    }

    @Override
    public Collection<User> getLoadedPlayers() {
        return loadedUsers;
    }

}
