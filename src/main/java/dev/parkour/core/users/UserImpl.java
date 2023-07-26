package dev.parkour.core.users;

import dev.parkour.Parkour;
import dev.parkour.api.Storage;
import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.map.enums.CompletionReason;
import dev.parkour.api.map.events.UserGameStartEvent;
import dev.parkour.api.map.sessions.ParkourSession;
import dev.parkour.api.users.User;
import dev.parkour.core.manager.ParkourSessionImpl;
import dev.parkour.records.MapRecord;
import dev.parkour.records.MapRecordImpl;
import lombok.NonNull;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserImpl implements User {

    private UUID uuid;
    private final Map<ParkourMap, MapRecordImpl> recordMap = new HashMap<>();
    private ParkourSession session;

    public UserImpl(UUID uuid){
        this.uuid = uuid;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }


    @Override
    public MapRecord getMapRecord(ParkourMap map) {
        return null;
    }

    @Override
    public CompletableFuture<MapRecord> setLowestRecord(ParkourMap map, long timeTaken) {
        MapRecord mapRecord = getMapRecord(map);
        if (mapRecord == null) {
            mapRecord = new MapRecordImpl(this, map);
            recordMap.put(map, (MapRecordImpl) mapRecord);
        }
        final MapRecordImpl impl = (MapRecordImpl) mapRecord;
        final Storage<?> storageEngine = Parkour.getInstance().getStorage();
        return CompletableFuture.supplyAsync(() -> {
            impl.setLowestRecordCache(timeTaken);
            storageEngine.updateRecord(uuid(), impl).join();
            return impl;
        });
    }

    @Override
    public ParkourSession Session() {
        return session;
    }

    @Override
    public ParkourSession setCurrentSession(@NonNull ParkourMap map) {
        if(session != null){
            if(!session.endSession(CompletionReason.ForcedCompletionReason)){
                return session;
            }
        }
        ParkourSessionImpl parkourSession = new ParkourSessionImpl(this,map);
        UserGameStartEvent gameStartEvent = new UserGameStartEvent(this,session);
        Bukkit.getPluginManager().callEvent(gameStartEvent);
        if(gameStartEvent.isCancelled()){
            return null;
        }
        this.session = parkourSession;
        return parkourSession;
    }

    public void cacheData(ParkourMap parkourMap,int timetaken,int completions,int gamesPlayed){
        if (!recordMap.containsKey(parkourMap)) {
            MapRecordImpl implementation = new MapRecordImpl(this, parkourMap);
            implementation.setGamesPlayedCache(gamesPlayed);
            implementation.setCompletionsCache(completions);
            implementation.setLowestRecordCache(timetaken);
            recordMap.put(parkourMap,implementation);
        }
    }
    public void updateSessionCache(ParkourSession session) {
        this.session = session;
    }
}
