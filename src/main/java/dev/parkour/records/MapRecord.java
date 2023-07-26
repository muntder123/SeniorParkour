package dev.parkour.records;

import dev.parkour.api.map.ParkourMap;

public interface MapRecord {

    /**
     * @return The recorded map.
     */
    ParkourMap getMap();

    /**
     * @return Lowest taken time to finish the map.
     */
    long getLowestRecord();

    /**
     * Updates the lowest taken time into the memory
     * and then save it into the database.
     *
     * @param record New record to set.
     */
    void setLowestRecord(long record);

    /**
     * @return Map completions times.
     */
    int getCompletions();

    /**
     * Updates completions times.
     *
     * @param completions New completions times value.
     */
    void setCompletions(int completions);

    /**
     * @return How many times did the player play on this map.
     */
    int getGamesPlayed();

    /**
     * Updates how many times did the player play in this map,
     * and save it into the database.
     *
     * @param gamesPlayed New value to set.
     */
    void setGamesPlayed(int gamesPlayed);
}