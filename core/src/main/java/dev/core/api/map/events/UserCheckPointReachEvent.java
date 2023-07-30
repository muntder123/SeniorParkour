package dev.core.api.map.events;

import dev.core.api.users.User;
import dev.core.core.manager.ParkourSession;
import dev.core.maps.points.PointMap;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class UserCheckPointReachEvent extends UserGameEvent implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private boolean cancel = false;
    private PointMap checkPoint;

    /**
     * This constructor holds the required information to pass the event.
     *
     * @param player Who reached the checkpoint.
     * @param session The current {@link ParkourSession} of the player.
     */
    public UserCheckPointReachEvent(User player, ParkourSession session, PointMap checkPoint) {
        super(player, session);
        this.checkPoint = checkPoint;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    /**
     * Gets the {@link PointMap} which the {@link User} reached on
     * this event.
     *
     * @return Reached checkpoint.
     */
    public PointMap getCheckPoint() {
        return this.checkPoint;
    }

    /**
     * Updates the reached {@link PointMap} on the current event to be passed
     * later if the event is not cancelled.
     *
     * @param checkPoint The new reached {@link PointMap}
     */
    public void setCheckPoint(PointMap checkPoint) {
        this.checkPoint = checkPoint;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}