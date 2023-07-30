package dev.core.api;

import dev.core.api.map.ParkourMap;
import dev.core.api.users.User;
import dev.core.records.MapRecord;
import dev.core.storage.TopPlayerData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Storage<P extends AutoCloseable> {

    void init(Config config);

    P getConnections();
    CompletableFuture<User> fetchRecordNow(UUID uuid);

    CompletableFuture<Void> updateRecord(UUID uuid, MapRecord record);

    CompletableFuture<Void> clearRecords(ParkourMap map);
    CompletableFuture<Integer> getPlayersWithBetterTimeCount(String mapName, long playerBestTime);
    Map<UUID, TopPlayerData> getTopPlayers(ParkourMap mapName);
}
