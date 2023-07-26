package dev.parkour.api.map.events;

import dev.parkour.api.users.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerGame extends Event {
    private final User user;

    public PlayerGame(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

}
