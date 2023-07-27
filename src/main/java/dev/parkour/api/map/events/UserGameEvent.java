package dev.parkour.api.map.events;

import dev.parkour.api.users.User;
import dev.parkour.core.manager.ParkourSession;

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
