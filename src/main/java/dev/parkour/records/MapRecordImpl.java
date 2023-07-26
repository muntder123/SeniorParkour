package dev.parkour.records;

import dev.parkour.Parkour;
import dev.parkour.api.Storage;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.core.users.UserImpl;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapRecordImpl implements MapRecord {

    private final UserImpl player;
    private final ParkourMap map;
    private long lowestRecord;
    private int completions = 0, gamesPlayed = 1;

    /**
     * @return The recorded map.
     */
    @Override
    public ParkourMap getMap() {
        return this.map;
    }

    /**
     * @return Lowest taken time to finish the map.
     */
    @Override
    public long getLowestRecord() {
        return lowestRecord;
    }

    /**
     * @return Map completions times.
     */
    @Override
    public int getCompletions() {
        return completions;
    }

    /**
     * Updates completions times.
     *
     * @param completions New completions times value.
     */
    @Override
    public void setCompletions(int completions) {
        // Update in cache
        setCompletionsCache(completions);
        // Update in database
    }

    /**
     * @return How many times did the player play on this map.
     */
    @Override
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Updates how many times did the player play in this map,
     * and save it into the database.
     *
     * @param gamesPlayed New value to set.
     */
    @Override
    public void setGamesPlayed(int gamesPlayed) {
        // Update in cache
        setGamesPlayedCache(gamesPlayed);
        // Save it in the database
        Storage<?> storageEngine = Parkour.getInstance().getStorage();
        storageEngine.updateRecord(player.uuid(), this)
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    /**
     * This one will attempt to update completions times in
     * memory only, it's only used for implementation
     * purposes so please ignore it.
     *
     * @param completions New value to cache.
     */
    public void setCompletionsCache(int completions) {
        this.completions = completions;
    }

    /**
     * Updates the lowest taken time into the memory
     * and then save it into the database.
     *
     * @param record New record to set.
     */
    @Override
    public void setLowestRecord(long record) {
        // Update it in memory
        setLowestRecordCache(record);
        // Save it in the database
        Storage<?> storageEngine = Parkour.getInstance().getStorage();
        storageEngine.updateRecord(player.uuid(), this)
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    /**
     * This one will attempt to update the lowest record
     * in memory only, it's only used for implementation
     * purposes so please ignore it.
     *
     * @param lowestRecord New value to cache.
     */
    public void setLowestRecordCache(long lowestRecord) {
        this.lowestRecord = lowestRecord;
    }

    /**
     * This one will attempt to update the games played
     * in memory only, it's only used for implementation
     * purposes so please ignore it.
     *
     * @param gamesPlayed New value to cache.
     */
    public void setGamesPlayedCache(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
}
