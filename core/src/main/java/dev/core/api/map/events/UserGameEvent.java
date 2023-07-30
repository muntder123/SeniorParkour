package dev.core.api.map.events;

import dev.core.api.users.User;
import dev.core.core.manager.ParkourSession;

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
