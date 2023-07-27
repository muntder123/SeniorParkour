package dev.parkour.records;

import dev.parkour.Parkour;
import dev.parkour.api.Storage;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.core.users.UserImpl;
import lombok.RequiredArgsConstructor;

public class MapRecord {

    private final UserImpl player;
    private final ParkourMap map;
    private long lowestRecord;
    private int completions = 0;
    private int gamesPlayed = 1;


    public MapRecord(UserImpl user,ParkourMap parkourMap){
        this.player = user;
        this.map = parkourMap;
    }

    public ParkourMap getMap() {
        return this.map;
    }

    public long getLowestRecord() {
        return lowestRecord;
    }

    public int getCompletions() {
        return completions;
    }


    public void setCompletions(int completions) {
        // Update in cache
        setCompletionsCache(completions);
        // Update in database
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

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


    public void setCompletionsCache(int completions) {
        this.completions = completions;
    }

    /**
     * Updates the lowest taken time into the memory
     * and then save it into the database.
     *
     * @param record New record to set.
     */
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
