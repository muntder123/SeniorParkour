package dev.parkour.api.map.events;

import dev.parkour.api.map.sessions.ParkourSession;
import dev.parkour.api.users.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class UserGameEvent extends PlayerGame{

    private final ParkourSession parkourSession;

    public UserGameEvent(User player, ParkourSession session) {
        super(player);
        this.parkourSession = session;
    }

    public ParkourSession getParkourSession() {
        return parkourSession;
    }
}
