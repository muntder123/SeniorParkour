package dev.core.api.map.events;

import dev.core.api.map.enums.CompletionReason;
import dev.core.api.users.User;
import dev.core.core.manager.ParkourSession;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class UserGameEndEvent extends UserGameEvent implements Cancellable {

    private final static HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;
    private CompletionReason reason;

    public UserGameEndEvent(User player, ParkourSession session, CompletionReason cause) {
        super(player, session);
        this.reason = cause;
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public CompletionReason getCause() {
        return reason;
    }

    /**
     * Updates the end cause in the current event.
     *
     * @param cause The new cause to set.
     */
    public void setCause(final CompletionReason cause) {
        this.reason = cause;
    }
}
