package dev.parkour.core.manager;

import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.enums.CompletionReason;
import dev.parkour.api.map.events.UserGameEndEvent;
import dev.parkour.api.map.sessions.ParkourSession;
import dev.parkour.api.users.User;
import dev.parkour.core.users.UserImpl;
import dev.parkour.maps.points.PointMap;
import org.bukkit.Bukkit;

public class ParkourSessionImpl implements ParkourSession {
    private final UserImpl user;
    private final ParkourMap currentMap;
    private final long startTime;
    private int currentCheckpointIndex;

    public ParkourSessionImpl(final UserImpl user,ParkourMap currentMap) {
        this.user = user;
        this.currentMap = currentMap;
        this.startTime = System.currentTimeMillis();
        this.currentCheckpointIndex = 0;
    }
    @Override
    public ParkourMap getCurrentMap() {
        return currentMap;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public int getCurrentCheckpoint() {
        return currentCheckpointIndex;
    }

    @Override
    public boolean setCurrentCheckpoint(PointMap checkpoint) {
        int newCheckpointIndex = currentMap.getPoints().indexOf(checkpoint);
        if (newCheckpointIndex >= 0) {
            this.currentCheckpointIndex = newCheckpointIndex;
            return true;
        }
        return false;
    }

    @Override
    public boolean endSession(CompletionReason cause) {
        UserGameEndEvent gameEndEvent = new UserGameEndEvent(user,this,cause);
        //Implement a event to end it
        Bukkit.getPluginManager().callEvent(gameEndEvent);
        if (gameEndEvent.isCancelled())
            return false;
        if (cause == CompletionReason.NormalCompletionReason) {
            long takenTime = System.currentTimeMillis() - startTime;
            if (user.hasFinished(currentMap)) {
                if (takenTime > user.getMapRecord(currentMap).getLowestRecord()) {
                    user.setLowestRecord(currentMap, takenTime);
                }
            } else {
                user.setLowestRecord(currentMap, takenTime);
            }
        }
        user.updateSessionCache(null);
        return false;
    }
}
