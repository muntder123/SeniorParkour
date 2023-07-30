package dev.core.api.users;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserManager {

    void LoadAllUsers();
    CompletableFuture<User> loadPlayer(UUID uuid);

    User getPlayer(UUID uuid);

    void cache(User player);

    Collection<User> getLoadedPlayers();
}
