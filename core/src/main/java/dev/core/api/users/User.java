package dev.core.api.users;



import dev.core.api.map.ParkourMap;
import dev.core.core.manager.ParkourSession;
import dev.core.records.MapRecord;
import lombok.NonNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface User {

    UUID uuid();


    MapRecord getMapRecord(ParkourMap map);
    CompletableFuture<MapRecord> setLowestRecord(ParkourMap map, long timeTaken);

    ParkourSession Session();
    ParkourSession setCurrentSession(@NonNull ParkourMap map);
    default boolean hasFinished(ParkourMap map) {
        return getMapRecord(map) != null && getMapRecord(map).getLowestRecord() != -1;
    }
    default boolean isEligable(ParkourMap map) {
        return hasFinished(map);
    }
    void updateSessionCache(ParkourSession session);
    //boolean endSession(final ParkourSessionEndCause cause);
}
