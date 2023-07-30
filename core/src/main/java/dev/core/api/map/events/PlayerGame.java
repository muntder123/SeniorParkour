package dev.core.api.map.events;

import dev.core.api.users.User;
import org.bukkit.event.Event;

public abstract class PlayerGame extends Event {
    private final User user;

    public PlayerGame(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

}
