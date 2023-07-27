package dev.parkour.core.users;

import dev.parkour.Parkour;
import dev.parkour.api.Storage;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.enums.CompletionReason;
import dev.parkour.api.map.events.UserGameStartEvent;
import dev.parkour.api.users.User;
import dev.parkour.core.manager.ParkourSession;
import dev.parkour.records.MapRecord;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserImpl implements User {

    private UUID uuid;
    private final Map<ParkourMap, MapRecord> recordMap = new HashMap<>();
    private ParkourSession currentSession;

    public UserImpl(UUID uuid){
        this.uuid = uuid;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }


    @Override
    public MapRecord getMapRecord(ParkourMap map) {
        return recordMap.get(map);
    }
    @Override
    public CompletableFuture<Integer> getPlayerPosition(String mapName, long playerBestTime) {
        final Storage<?> storageEngine = Parkour.getInstance().getStorage();
        return storageEngine.getPlayersWithBetterTimeCount(mapName, playerBestTime)
                .thenApply(playersWithBetterTime -> playersWithBetterTime + 1);
    }

    @Override
    public CompletableFuture<MapRecord> setLowestRecord(ParkourMap map, long timeTaken) {
        MapRecord mapRecord = getMapRecord(map);
        if (mapRecord == null) {
            mapRecord = new MapRecord(this, map);
            recordMap.put(map, (MapRecord) mapRecord);
        }
        final MapRecord impl = (MapRecord) mapRecord;
        final Storage<?> storageEngine = Parkour.getInstance().getStorage();
        return CompletableFuture.supplyAsync(() -> {
            impl.setLowestRecordCache(timeTaken);
            storageEngine.updateRecord(uuid(), impl).join();
            return impl;
        });
    }

    @Override
    public ParkourSession Session() {
        return currentSession;
    }

    @Override
    public ParkourSession setCurrentSession(@NonNull ParkourMap map) {
        if(currentSession != null){
            if(!currentSession.endSession(CompletionReason.ForcedCompletionReason)){
                return currentSession;
            }
        }
        ParkourSession session = new ParkourSession(this,map);
        UserGameStartEvent gameStartEvent = new UserGameStartEvent(this,session);
        Bukkit.getPluginManager().callEvent(gameStartEvent);
        if(gameStartEvent.isCancelled()){
            return null;
        }
        this.currentSession = session;
        System.out.println("Session is first one is " + currentSession + " second one is + " + session);
        return session;
    }

    public void cacheData(ParkourMap parkourMap,int timetaken,int completions,int gamesPlayed){
        if (!recordMap.containsKey(parkourMap)) {
            MapRecord implementation = new MapRecord(this, parkourMap);
            implementation.setGamesPlayedCache(gamesPlayed);
            implementation.setCompletionsCache(completions);
            implementation.setLowestRecordCache(timetaken);
            recordMap.put(parkourMap,implementation);
        }
    }
    public void updateSessionCache(ParkourSession session) {
        this.currentSession = session;
    }
}
