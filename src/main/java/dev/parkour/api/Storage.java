package dev.parkour.api;

import dev.parkour.api.map.ParkourMap;
import dev.parkour.api.users.User;
import dev.parkour.records.MapRecord;
import dev.parkour.storage.TopPlayerData;

import java.util.List;
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
    Map<UUID,TopPlayerData> getTopPlayers(ParkourMap mapName);
}
