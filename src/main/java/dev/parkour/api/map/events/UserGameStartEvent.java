package dev.parkour.api.map.events;

import dev.parkour.api.users.User;
import dev.parkour.core.manager.ParkourSession;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserGameStartEvent extends UserGameEvent implements Cancellable {
    private final static HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;

    public UserGameStartEvent(User player, ParkourSession session) {
        super(player, session);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
