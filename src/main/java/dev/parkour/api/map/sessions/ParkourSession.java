package dev.parkour.api.map.sessions;

import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.enums.CompletionReason;
import dev.parkour.maps.points.CheckPointMap;
import dev.parkour.maps.points.PointMap;

public interface ParkourSession {

    /**
     * Gets the current parkour map being played.
     *
     * @return The current parkour map.
     */
    ParkourMap getCurrentMap();

    /**
     * Gets the start time of the parkour session.
     *
     * @return The start time of the parkour session in milliseconds since epoch.
     */
    long getStartTime();

    /**
     * Gets the current checkpoint reached in the parkour map.
     *
     * @return The index of the current checkpoint reached.
     */
    int getCurrentCheckpoint();

    /**
     * Gets the current checkpoint object that the player is standing on.
     *
     * @return The current checkpoint object the player is standing on.
     */

    /**
     * Updates the current checkpoint that the player is standing on.
     *
     * @param checkpoint The new checkpoint to update for the player.
     * @return If the checkpoint update is successful.
     */
    boolean setCurrentCheckpoint(CheckPointMap checkpoint);

    /**
     * Attempts to end the user's session.
     *
     * @param cause The cause of session end.
     * @return If the session is successfully ended.
     */
    boolean endSession(CompletionReason cause);

    /**
     * Checks if the session has ended.
     *
     * @return True if the session has ended, false otherwise.
     */
}